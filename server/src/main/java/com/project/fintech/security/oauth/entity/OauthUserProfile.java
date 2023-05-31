package com.project.fintech.security.oauth.entity;

import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.project.fintech.*;
import com.project.fintech.model.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce.Cluster.Refresh;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OauthUserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String oauthType;
    private String oauthId;
    private String name;
    private String email;
    private String imageUrl;
    private String refreshToken;

    public Users toMember(String oauthType, String oauthId, String email, String username, String imageUrl) {
        String userId = oauthType + "00000"+ this.id;
        return new Users(userId, oauthType, oauthId, email, username, imageUrl, refreshToken);
    }

}
