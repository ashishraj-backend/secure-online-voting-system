package com.example.voting.config;

import com.example.voting.entity.Candidate;
import com.example.voting.entity.Election;
import com.example.voting.entity.Role;
import com.example.voting.entity.User;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.ElectionRepository;
import com.example.voting.repository.RoleRepository;
import com.example.voting.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
public class DataLoader {
    @Bean
    public CommandLineRunner seed(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder, ElectionRepository electionRepo, CandidateRepository candidateRepo) {
        return args -> {
            if (roleRepo.findByName("ADMIN").isEmpty()) roleRepo.save(new Role("ADMIN"));
            if (roleRepo.findByName("VOTER").isEmpty()) roleRepo.save(new Role("VOTER"));
            if (userRepo.findByEmail("admin@example.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(encoder.encode("AdminPass123"));
                var role = roleRepo.findByName("ADMIN").get();
                admin.getRoles().add(role);
                userRepo.save(admin);
            }

            if (electionRepo.count() == 0) {
                Election e = new Election();
                e.setTitle("Sample Election");
                e.setDescription("A sample election for development");
                e.setStartTime(Instant.now());
                e.setEndTime(Instant.now().plusSeconds(3600));
                e.setStatus(Election.Status.LIVE);
                electionRepo.save(e);
                Candidate c1 = new Candidate("Candidate A");
                c1.setElection(e);
                Candidate c2 = new Candidate("Candidate B");
                c2.setElection(e);
                candidateRepo.save(c1);
                candidateRepo.save(c2);
            }
        };
    }
}
