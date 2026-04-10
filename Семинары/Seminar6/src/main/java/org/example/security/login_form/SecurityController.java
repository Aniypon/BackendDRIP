package org.example.security.login_form;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public method";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')") // Только пользователи с ролью USER
    public String userEndpoint() {
        return "Вы вошли как USER.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Только пользователи с ролью ADMIN
    public String adminEndpoint() {
        return "Вы вошли как ADMIN.";
    }

    @GetMapping("/user-or-admin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // USER или ADMIN
    public String userOrAdminEndpoint() {
        return "Доступно для USER или ADMIN.";
    }
}
