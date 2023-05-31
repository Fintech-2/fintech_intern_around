package com.project.fintech.security.oauth.utils;

import com.project.fintech.security.oauth.entity.*;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;


// 플랫폼 별 OauthUserProfile에 데이터 매핑 enum
public enum OAuthAttributes {
    GOOGLE("google", (attributes) -> {

        return OauthUserProfile.builder()
                .oauthType("GL")
                .oauthId(String.valueOf(attributes.get("sub")))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .imageUrl((String) attributes.get("picture"))
                .build();
    }),

    KAKAO("kakao", (attributes) -> {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile"); // 이미지 profile_image_url

        return OauthUserProfile.builder()
                .oauthType("KA")
                .oauthId(String.valueOf(attributes.get("id")))
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .imageUrl((String) profile.get("profile_image_url"))
                .build();


    }),

    NAVER("naver", (attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OauthUserProfile.builder()
                .oauthType("NA")
                .oauthId((String) response.get("id"))
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .imageUrl((String) response.get("profile_image"))
                .build();
    });

    private final String registrationId;
    private final Function<Map<String, Object>, OauthUserProfile> of;

    OAuthAttributes(String registrationId, Function<Map<String, Object>, OauthUserProfile> of) {
        this.registrationId = registrationId;
        this.of = of;
    }

    public static OauthUserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of.apply(attributes);
    }

}
