package org.example.security.jwt;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static org.hibernate.internal.util.collections.CollectionHelper.listOf;

@RestController
@AllArgsConstructor
public class AuthController {
    private AuthenticationManager authenticationManager;

    @PostMapping("/auth/login")
    public String authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.username(),
                loginRequest.password()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = Jwts.builder()
            .subject("user")
            .claim("roles", listOf("ROLE_USER"))
            .expiration(new Date(System.currentTimeMillis() + 864_000_000))
            .signWith(JwtFilter.SECRET_KEY)
            .compact();

        return "Bearer " + token;
    }

    public record LoginRequest(String username, String password) {
    }
}