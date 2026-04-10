package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.api.client.UserApi;
import org.example.model.client.CreateUserRequest;
import org.example.model.client.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserClientService {

    private final UserApi userApi;

    public User getUserById(Integer id) {
        return userApi.getUserById(id).getBody();
    }
}
