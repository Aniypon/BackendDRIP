package org.example.userservice.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // In-memory store for idempotency keys (in production, use Redis or database)
    private final ConcurrentHashMap<String, Boolean> processedIdempotencyKeys = new ConcurrentHashMap<>();
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public List<User> searchUsersByEmail(String email) {
        return userRepository.findByEmailContaining(email);
    }
    
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Обновляет активность пользователя с поддержкой идемпотентности
     * @param id ID пользователя
     * @param lastActivity Временная метка последней активности
     * @param idempotencyKey Ключ идемпотентности для обеспечения идемпотентности операции
     * @return Обновленный пользователь
     */
    public User updateUserActivity(Long id, String lastActivity, String idempotencyKey) {
        // Check if this operation has already been processed
        if (idempotencyKey != null && processedIdempotencyKeys.containsKey(idempotencyKey)) {
            // Return the existing user data (idempotent operation)
            return userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setLastActivity(lastActivity);
        User updatedUser = userRepository.save(user);
        
        // Mark this operation as processed if idempotency key is provided
        if (idempotencyKey != null) {
            processedIdempotencyKeys.put(idempotencyKey, true);
        }
        
        return updatedUser;
    }
    
    /**
     * Updates user activity without idempotency key (backward compatibility)
     */
    public User updateUserActivity(Long id, String lastActivity) {
        return updateUserActivity(id, lastActivity, null);
    }
}
