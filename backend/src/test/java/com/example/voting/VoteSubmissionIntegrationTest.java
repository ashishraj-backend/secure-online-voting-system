package com.example.voting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VoteSubmissionIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void voterCanRequestAuthorizationAndVote() throws Exception {
        // login as admin to create an election
        var adminLogin = Map.of("email","admin@example.com","password","AdminPass123");
        var adminRes = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(adminLogin))).andExpect(status().isOk()).andReturn();
        var adminToken = mapper.readTree(adminRes.getResponse().getContentAsString()).get("token").asText();

        // create election
        var election = Map.of("title","IT Test Election","description","desc");
        var createRes = mvc.perform(post("/api/admin/elections").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(election)).header("Authorization","Bearer "+adminToken)).andExpect(status().isOk()).andReturn();
        var electionId = mapper.readTree(createRes.getResponse().getContentAsString()).get("id").asText();

        // add candidate
        var candidate = Map.of("name","Candidate X");
        var addC = mvc.perform(post("/api/admin/elections/"+electionId+"/candidates").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(candidate)).header("Authorization","Bearer "+adminToken)).andExpect(status().isOk()).andReturn();
        var candidateId = mapper.readTree(addC.getResponse().getContentAsString()).get("id").asText();

        // register voter
        var reg = Map.of("name","VoterOne","email","voterone@example.com","password","Secret123");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(reg))).andExpect(status().isOk());
        var loginRes = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of("email","voterone@example.com","password","Secret123")))).andExpect(status().isOk()).andReturn();
        var voterToken = mapper.readTree(loginRes.getResponse().getContentAsString()).get("token").asText();

        // request authorization
        var authRes = mvc.perform(post("/api/elections/"+electionId+"/authorization").header("Authorization","Bearer "+voterToken)).andExpect(status().isOk()).andReturn();
        var va = mapper.readTree(authRes.getResponse().getContentAsString()).get("token").asText();

        // cast vote
        var vote = Map.of("candidate", Map.of("id", candidateId));
        mvc.perform(post("/api/elections/"+electionId+"/votes").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of("candidateId", candidateId))).header("Authorization","Bearer "+voterToken).header("Authorization-Token", va)).andExpect(status().isOk());
    }
}
