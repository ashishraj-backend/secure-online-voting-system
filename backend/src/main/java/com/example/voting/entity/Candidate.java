package com.example.voting.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidate {
    @Id
    @GeneratedValue
    private java.util.UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "election_id")
    private Election election;

    @Column(nullable = false)
    private String name;

    public Candidate() {}
    public Candidate(String name) {this.name = name;}

    public java.util.UUID getId() {return id;}
    public Election getElection() {return election;}
    public void setElection(Election election) {this.election = election;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}
