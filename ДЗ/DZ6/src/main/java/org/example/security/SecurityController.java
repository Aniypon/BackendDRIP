package org.example.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SecurityController {

    @GetMapping("/api/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "public ok");
    }

    @GetMapping("/api/user")
    public Map<String, String> userEndpoint(Authentication authentication) {
        return Map.of(
                "message", "authorized",
                "user", authentication.getName()
        );
    }
}
