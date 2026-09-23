package com.example.buddybackend.service;

import com.example.buddybackend.model.ExpiryAlertDto;
import com.example.buddybackend.model.PantryItemDto;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PantryService {

    private static final Logger log = LoggerFactory.getLogger(PantryService.class);

    @Autowired(required = false)
    private Firestore firestore;

    // In-memory fallback for local development or when cloud credentials aren't bound
    private final Map<String, Map<String, PantryItemDto>> inMemoryStore = new ConcurrentHashMap<>();

    public List<PantryItemDto> getPantryItems(String householdId) {
        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection("households")
                        .document(householdId)
                        .collection("pantry_items")
                        .get();
                List<QueryDocumentSnapshot> docs = future.get().getDocuments();
                List<PantryItemDto> items = new ArrayList<>();
                for (QueryDocumentSnapshot doc : docs) {
                    PantryItemDto dto = doc.toObject(PantryItemDto.class);
                    if (dto.getId() == null || dto.getId().isEmpty()) {
                        dto.setId(doc.getId());
                    }
                    items.add(dto);
                }
                return items;
            } catch (Exception e) {
                log.warn("Firestore read failed, falling back to local store: {}", e.getMessage());
            }
        }

        return new ArrayList<>(inMemoryStore.computeIfAbsent(householdId, k -> new ConcurrentHashMap<>()).values());
    }

    public PantryItemDto savePantryItem(String householdId, PantryItemDto item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId("item_" + System.currentTimeMillis());
        }
        item.setHouseholdId(householdId);
        item.setUpdatedAtMillis(System.currentTimeMillis());

        if (firestore != null) {
            try {
                DocumentReference docRef = firestore.collection("households")
                        .document(householdId)
                        .collection("pantry_items")
                        .document(item.getId());
                docRef.set(item, SetOptions.merge()).get();
            } catch (Exception e) {
                log.warn("Firestore write failed, saving in local fallback: {}", e.getMessage());
            }
        }

        inMemoryStore.computeIfAbsent(householdId, k -> new ConcurrentHashMap<>()).put(item.getId(), item);
        return item;
    }

    public boolean deletePantryItem(String householdId, String itemId) {
        if (firestore != null) {
            try {
                firestore.collection("households")
                        .document(householdId)
                        .collection("pantry_items")
                        .document(itemId)
                        .delete().get();
            } catch (Exception e) {
                log.warn("Firestore delete failed: {}", e.getMessage());
            }
        }

        Map<String, PantryItemDto> items = inMemoryStore.get(householdId);
        if (items != null) {
            return items.remove(itemId) != null;
        }
        return true;
    }

    public List<ExpiryAlertDto> calculateExpiryAlerts(String householdId) {
        List<PantryItemDto> items = getPantryItems(householdId);
        long now = System.currentTimeMillis();
        List<ExpiryAlertDto> alerts = new ArrayList<>();

        for (PantryItemDto item : items) {
            if (item.isConsumed()) continue;

            long diffMillis = item.getExpiryDateMillis() - now;
            long daysRemaining = diffMillis / (1000L * 60 * 60 * 24);

            String urgency;
            if (daysRemaining < 0) {
                urgency = "EXPIRED";
            } else if (daysRemaining <= 2) {
                urgency = "CRITICAL";
            } else if (daysRemaining <= 5) {
                urgency = "WARNING";
            } else {
                continue; // Not expiring soon
            }

            alerts.add(new ExpiryAlertDto(
                    item.getId(),
                    item.getName(),
                    item.getStorageZone(),
                    daysRemaining,
                    urgency
            ));
        }

        // Sort with most urgent first
        alerts.sort((a, b) -> Long.compare(a.getDaysRemaining(), b.getDaysRemaining()));
        return alerts;
    }
}
