package com.example.buddybackend.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.project-id:gen-lang-client-0095254671}")
    private String projectId;

    @Value("${firebase.database-id:ai-studio-homebuddy-dc50eb87-d59b-4396-bae5-9fae7a9f6be3}")
    private String databaseId;

    @Value("${firebase.credentials-path:firebase-service-account.json}")
    private String credentialsPath;

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.getInstance();
            }

            GoogleCredentials credentials;
            File file = new File(credentialsPath);
            if (file.exists()) {
                try (InputStream serviceAccount = new FileInputStream(file)) {
                    credentials = GoogleCredentials.fromStream(serviceAccount);
                }
            } else {
                log.info("Credentials file {} not found, using application default credentials.", credentialsPath);
                try {
                    credentials = GoogleCredentials.getApplicationDefault();
                } catch (Exception e) {
                    log.warn("Application default credentials not available, fallback to mock credentials for local development.");
                    throw new IllegalStateException("Firebase credentials not configured", e);
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();

            return FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            log.error("Failed to initialize FirebaseApp: {}", e.getMessage());
              throw new IllegalStateException("Failed to initialize Firebase", e);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
       return FirebaseAuth.getInstance(firebaseApp);
    }

    @Bean
    public Firestore firestore() {
        try {
            FirestoreOptions.Builder builder = FirestoreOptions.newBuilder()
                    .setProjectId(projectId);

            if (databaseId != null && !databaseId.isEmpty() && !databaseId.equals("(default)")) {
                builder.setDatabaseId(databaseId);
            }

            return builder.build().getService();
        } catch (Exception e) {
            log.warn("Unable to connect to live Firestore (requires cloud credentials). In-memory mock/local fallback will be available: {}", e.getMessage());
            return null;
        }
    }
}
