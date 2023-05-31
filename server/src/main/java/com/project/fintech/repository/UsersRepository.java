package com.project.fintech.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.fintech.model.*;

public interface UsersRepository extends JpaRepository<Users, Long> {
  Optional<Users> findByEmail(String email);
  Optional<Users> findBySnsToken(String snsToken);
  Users findTopByOrderByIdDesc();
}