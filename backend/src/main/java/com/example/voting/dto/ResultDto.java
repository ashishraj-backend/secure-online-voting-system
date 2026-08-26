package com.example.voting.dto;

public record ResultDto(String candidateId, String candidateName, long votes, double percentage) {}
