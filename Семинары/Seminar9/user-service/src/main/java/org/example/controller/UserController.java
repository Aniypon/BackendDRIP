package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.api.UserApi;
import org.example.entity.User;
import org.example.model.CreateUserRequest;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    
    private final UserService userService;
    
    @Override
    public ResponseEntity<org.example.model.User> getUserById(Integer id) {
        return userService.findById(id.longValue())
                .map(this::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Override
    public ResponseEntity<org.example.model.User> createUser(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        return ResponseEntity.ok(toModel(userService.save(user)));
    }
    
    private org.example.model.User toModel(User entity) {
        org.example.model.User model = new org.example.model.User();
        model.setId(entity.getId().intValue());
        model.setUsername(entity.getUsername());
        return model;
    }
}
