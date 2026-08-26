package com.example.voting.repository;

import com.example.voting.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ElectionRepository extends JpaRepository<Election, UUID> {
}
