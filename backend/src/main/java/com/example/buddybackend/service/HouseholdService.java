package com.example.buddybackend.service;

import com.example.buddybackend.entity.Household;
import com.example.buddybackend.repository.HouseholdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class HouseholdService {

    private final HouseholdRepository householdRepository;

    public HouseholdService(HouseholdRepository householdRepository) {
        this.householdRepository = householdRepository;
    }

    public Household create(Household household) {
        return householdRepository.save(household);
    }

    public Optional<Household> findById(UUID id) {
        return householdRepository.findById(id);
    }

    public Optional<Household> findByInviteCode(String inviteCode) {
        return householdRepository.findByInviteCode(inviteCode);
    }

    public List<Household> findByCreatedByUserId(UUID userId) {
        return householdRepository.findByCreatedByUserId(userId);
    }

    public List<Household> findAll() {
        return householdRepository.findAll();
    }

    public Household update(Household household) {
        return householdRepository.save(household);
    }

    public void delete(UUID id) {
        if (!householdRepository.existsById(id)) {
            throw new RuntimeException("Household not found");
        }

        householdRepository.deleteById(id);
    }
}