package com.example.buddybackend.cron;

import com.example.buddybackend.model.ExpiryAlertDto;
import com.example.buddybackend.service.PantryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpiryNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExpiryNotificationScheduler.class);

    @Autowired
    private PantryService pantryService;

    /**
     * Runs every morning at 8:00 AM (or every 60 minutes for demo/testing).
     * Calculates items about to expire and dispatches push notifications.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void runDailyExpiryCheck() {
        log.info("Starting scheduled daily pantry expiry scan...");
        String householdId = "default_household";
        List<ExpiryAlertDto> alerts = pantryService.calculateExpiryAlerts(householdId);

        if (alerts.isEmpty()) {
            log.info("No items expiring soon in household: {}", householdId);
            return;
        }

        for (ExpiryAlertDto alert : alerts) {
            log.info("ALERT: Item '{}' (Storage: {}) has {} days remaining [Urgency: {}]",
                    alert.getItemName(), alert.getStorageZone(), alert.getDaysRemaining(), alert.getUrgency());
            // In full production, this triggers Firebase Cloud Messaging (FCM):
            // Message message = Message.builder().setTopic("household_" + householdId)...
            // FirebaseMessaging.getInstance().send(message);
        }
    }
}
