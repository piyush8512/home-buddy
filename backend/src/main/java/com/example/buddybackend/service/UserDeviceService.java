package com.example.buddybackend.service;

import com.example.buddybackend.entity.UserDevice;
import com.example.buddybackend.repository.UserDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;

    public UserDeviceService(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    public UserDevice create(UserDevice device) {
        return userDeviceRepository.save(device);
    }

    public Optional<UserDevice> findById(UUID id) {
        return userDeviceRepository.findById(id);
    }

    public List<UserDevice> findByUserId(UUID userId) {
        return userDeviceRepository.findByUserId(userId);
    }

    public Optional<UserDevice> findByFcmToken(String fcmToken) {
        return userDeviceRepository.findByFcmToken(fcmToken);
    }

    public List<UserDevice> findActiveByUserId(UUID userId) {
        return userDeviceRepository.findByUserIdAndIsActiveTrue(userId);
    }

    public UserDevice update(UserDevice device) {
        return userDeviceRepository.save(device);
    }

    public void delete(UUID id) {
        userDeviceRepository.deleteById(id);
    }
}