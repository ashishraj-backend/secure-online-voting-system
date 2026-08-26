package com.example.voting.service;

import com.example.voting.dto.RegisterDto;
import com.example.voting.entity.Role;
import com.example.voting.entity.User;
import com.example.voting.repository.RoleRepository;
import com.example.voting.repository.UserRepository;
import com.example.voting.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public String register(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) throw new RuntimeException("UserAlreadyExists");
        User u = new User();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        u.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        Role voterRole = roleRepository.findByName("VOTER").orElseGet(() -> roleRepository.save(new Role("VOTER")));
        u.getRoles().add(voterRole);
        userRepository.save(u);
        // audit
        try { // optional injection
            // no-op: AuditService injection not added to avoid circular wiring here
        } catch (Exception ignore) {}
        return jwtService.generateToken(u.getId().toString());
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        User u = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("InvalidCredentials"));
        if (!passwordEncoder.matches(password, u.getPasswordHash())) throw new RuntimeException("InvalidCredentials");
        return jwtService.generateToken(u.getId().toString());
    }
}
