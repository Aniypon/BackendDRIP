package org.example.userservice.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam(required = false) String email,
                                 @RequestParam(required = false) String name) {
        if (email != null) {
            return userService.searchUsersByEmail(email);
        } else if (name != null) {
            return userService.searchUsersByName(name);
        } else {
            return userService.getAllUsers();
        }
    }
    
    /**
     * Конечная точка для обновления активности пользователя - используется order-service в паттерне Saga
     * Поддерживает идемпотентность через заголовок Idempotency-Key
     */
    @PutMapping("/{id}/activity")
    public ResponseEntity<User> updateUserActivity(@PathVariable Long id, 
                                                   @RequestBody Map<String, String> requestBody,
                                                   HttpServletRequest request) {
        try {
            String lastActivity = requestBody.get("lastActivity");
            String idempotencyKey = request.getHeader("Idempotency-Key");
            
            User updatedUser = userService.updateUserActivity(id, lastActivity, idempotencyKey);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
