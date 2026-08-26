package com.example.voting;

import com.example.voting.entity.Candidate;
import com.example.voting.entity.Election;
import com.example.voting.entity.User;
import com.example.voting.entity.Vote;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.ElectionRepository;
import com.example.voting.repository.UserRepository;
import com.example.voting.repository.VoteRepository;
import com.example.voting.repository.VotingAuthorizationRepository;
import com.example.voting.service.VotingAuthorizationService;
import com.example.voting.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrentVotingServiceTest {
    @Autowired UserRepository userRepository;
    @Autowired ElectionRepository electionRepository;
    @Autowired CandidateRepository candidateRepository;
    @Autowired VoteRepository voteRepository;
    @Autowired VotingAuthorizationRepository vaRepository;
    @Autowired VotingAuthorizationService vaService;
    @Autowired VotingService votingService;

    private User voter;
    private Election election;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        voteRepository.deleteAll();
        vaRepository.deleteAll();
        candidateRepository.deleteAll();
        electionRepository.deleteAll();
        userRepository.deleteAll();

        voter = new User();
        voter.setName("Race");
        voter.setEmail("race@example.com");
        voter.setPasswordHash("x");
        userRepository.save(voter);

        election = new Election();
        election.setTitle("RaceElection");
        election.setStatus(Election.Status.LIVE);
        electionRepository.save(election);

        candidate = new Candidate();
        candidate.setName("Cand");
        candidate.setElection(election);
        candidateRepository.save(candidate);
    }

    @Test
    void multipleSimultaneousRequests_onlyOneVotes() throws InterruptedException {
        String token = vaService.createAuthorization(voter, election);
        int threads = 50;
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i=0;i<threads;i++) {
            futures.add(ex.submit(() -> {
                latch.await();
                try {
                    Vote v = new Vote();
                    v.setVoter(voter);
                    v.setElection(election);
                    v.setCandidate(candidate);
                    votingService.castVote(voter, v, token);
                    return true;
                } catch (Exception e) {
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
