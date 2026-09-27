package com.example.buddybackend.controller;

import com.example.buddybackend.entity.User;
import com.example.buddybackend.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // CURRENT USER
    // =========================

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
            Authentication authentication
    ) {

        String firebaseUid = authentication.getName();

        try {

            FirebaseToken token =
                    FirebaseAuth.getInstance().verifyIdToken(
                            authentication.getCredentials() != null
                                    ? authentication.getCredentials().toString()
                                    : ""
                    );

            User user = userService.findOrCreate(
                    firebaseUid,
                    token.getEmail(),
                    token.getName(),
                    token.getPicture()
            );

            return ResponseEntity.ok(user);

        } catch (Exception e) {

            return ResponseEntity.status(401).build();
        }
    }

    // =========================
    // FIND USER BY DATABASE UUID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @PathVariable UUID id
    ) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}