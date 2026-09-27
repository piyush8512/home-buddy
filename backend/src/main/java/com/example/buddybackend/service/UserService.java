package com.example.buddybackend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.buddybackend.entity.User;
import com.example.buddybackend.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//find
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

//create login
   public User findOrCreate(
        String firebaseUid,
        String email,
        String displayName,
        String avatarUrl
   ) {
    return userRepository.findByFirebaseUid(firebaseUid)
            .orElseGet(() -> {

                User user = new User();

                user.setFirebaseUid(firebaseUid);
                user.setEmail(email);
                user.setDisplayName(displayName);
                user.setAvatarUrl(avatarUrl);
                user.setIs_active(true);

                return userRepository.save(user);
            });
        }


    //create user
    public User create(User user) {
        return userRepository.save(user);
    }


    // updare user
    public User update(User user) {
        return userRepository.save(user);
    }

    public User updateProfile(
            UUID id,
            String displayName,
            String avatarUrl
    ) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // user.setDisplayName(displayName);
        // user.setAvatarUrl(avatarUrl);

        return userRepository.save(user);
    }


    //acitivating and deactiviating user
    // public User activate(UUID id) {

    //     User user = userRepository.findById(id)
    //             .orElseThrow(() ->
    //                     new RuntimeException("User not found")
    //             );

    //     user.setIs_active(true);

    //     return userRepository.save(user);
    // }

    // public User deactivate(UUID id) {

    //     User user = userRepository.findById(id)
    //             .orElseThrow(() ->
    //                     new RuntimeException("User not found")
    //             );

    //     user.setIs_active(false);

    //     return userRepository.save(user);
    // }

    //delete user
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

     //exists checking
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    public boolean existsByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}