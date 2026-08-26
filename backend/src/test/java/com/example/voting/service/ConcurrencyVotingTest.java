package com.example.voting.service;

import com.example.voting.entity.Candidate;
import com.example.voting.entity.Election;
import com.example.voting.entity.User;
import com.example.voting.entity.Vote;
import com.example.voting.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
public class ConcurrencyVotingTest {
    @Autowired
    private VoteRepository voteRepository;

    private User voter;
    private Election election;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        voter = new User();
        voter.setName("Test");
        voter.setEmail("t@example.com");
        election = new Election();
        election.setTitle("Test Elec");
        candidate = new Candidate("A");
        candidate.setElection(election);
    }

    @Test
    void concurrentVotes_onlyOneSucceeds() throws InterruptedException {
        int threads = 50;
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(ex.submit(() -> {
                latch.await();
                try {
                    Vote v = new Vote();
                    v.setVoter(voter);
                    v.setElection(election);
                    v.setCandidate(candidate);
                    voteRepository.save(v);
                    return true;
                } catch (Exception exx) {
                    return false;
                }
            }));
        }
        latch.countDown();
        int success = 0;
        for (Future<Boolean> f : futures) {
            try { if (f.get()) success++; } catch (Exception ignored) {}
        }
        assertThat(success).isEqualTo(1);
    }
}
