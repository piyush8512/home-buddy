package com.example.buddybackend.repository;

import com.example.buddybackend.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    List<UserDevice> findByUserId(UUID userId);

    Optional<UserDevice> findByFcmToken(String fcmToken);

    List<UserDevice> findByUserIdAndIsActiveTrue(UUID userId);
}
