package com.example.voting.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "voting_authorizations", indexes = {@Index(columnList = "token", name = "idx_va_token")})
public class VotingAuthorization {
    @Id
    @GeneratedValue
    private java.util.UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voter_id")
    private User voter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "election_id")
    private Election election;

    @Column(nullable = false, unique = true)
    private String token;

    private Instant expiresAt;

    private boolean used = false;

    public java.util.UUID getId() {return id;}
    public User getVoter() {return voter;}
    public void setVoter(User voter) {this.voter = voter;}
    public Election getElection() {return election;}
    public void setElection(Election election) {this.election = election;}
    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}
    public Instant getExpiresAt() {return expiresAt;}
    public void setExpiresAt(Instant expiresAt) {this.expiresAt = expiresAt;}
    public boolean isUsed() {return used;}
    public void setUsed(boolean used) {this.used = used;}
}
