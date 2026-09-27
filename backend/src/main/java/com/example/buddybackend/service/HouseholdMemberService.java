package com.example.buddybackend.service;

import com.example.buddybackend.entity.HouseholdMember;
import com.example.buddybackend.repository.HouseholdMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class HouseholdMemberService {

    private final HouseholdMemberRepository repository;

    public HouseholdMemberService(HouseholdMemberRepository repository) {
        this.repository = repository;
    }

    public HouseholdMember create(HouseholdMember member) {
        return repository.save(member);
    }

    public Optional<HouseholdMember> findById(UUID id) {
        return repository.findById(id);
    }

    public List<HouseholdMember> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    public List<HouseholdMember> findByHouseholdId(UUID householdId) {
        return repository.findByHouseholdId(householdId);
    }

    public Optional<HouseholdMember> findMembership(
            UUID householdId,
            UUID userId
    ) {
        return repository.findByHouseholdIdAndUserId(
                householdId,
                userId
        );
    }

    public boolean isMember(UUID householdId, UUID userId) {
        return repository.existsByHouseholdIdAndUserId(
                householdId,
                userId
        );
    }

    public HouseholdMember update(HouseholdMember member) {
        return repository.save(member);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}