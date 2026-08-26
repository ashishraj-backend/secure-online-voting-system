package com.example.voting.controller;

import com.example.voting.dto.RegisterDto;
import com.example.voting.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final com.example.voting.service.RateLimitService rateLimitService;

    public AuthController(AuthService authService, com.example.voting.service.RateLimitService rateLimitService) {this.authService = authService; this.rateLimitService = rateLimitService;}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto dto) {
        String key = "rl:register:" + dto.getEmail();
        if (!rateLimitService.incrementAndCheck(key, 5, java.time.Duration.ofMinutes(10))) throw new com.example.voting.exception.RateLimitExceededException("Too many registrations");
        String token = authService.register(dto);
        return ResponseEntity.ok().body(java.util.Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody com.example.voting.dto.AuthDto dto) {
        String key = "rl:login:" + dto.email();
        if (!rateLimitService.incrementAndCheck(key, 10, java.time.Duration.ofMinutes(5))) throw new com.example.voting.exception.RateLimitExceededException("Too many login attempts");
        String token = authService.login(dto.email(), dto.password());
        return ResponseEntity.ok(java.util.Map.of("token", token));
    }
}
