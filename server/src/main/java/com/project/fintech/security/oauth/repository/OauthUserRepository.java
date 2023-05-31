package com.project.fintech.security.oauth.repository;

import com.project.fintech.security.oauth.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface OauthUserRepository extends JpaRepository<OauthUserProfile, Long> {
    Optional<OauthUserProfile> findByOauthId(String oauthId);
}
