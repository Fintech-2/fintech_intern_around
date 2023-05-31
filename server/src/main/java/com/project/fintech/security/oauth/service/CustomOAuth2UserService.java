package com.project.fintech.security.oauth.service;

import com.project.fintech.model.Users;
import com.project.fintech.repository.UsersRepository;
import com.project.fintech.security.oauth.entity.OauthUserProfile;
import com.project.fintech.security.oauth.repository.OauthUserRepository;
import com.project.fintech.security.oauth.utils.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsersRepository usersRepository;
    private final OauthUserRepository oauthUserRepository;

    // // JWT 토큰 발급
    // public String makeJwtToken() {
    //     Date now = new Date();
    
    //     return Jwts.builder()
    //         .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
    //         .setIssuer("fresh") // (2)
    //         .setIssuedAt(now) // (3)
    //         .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis())) // (4)
    //         .claim("id", "아이디") // (5)
    //         .claim("email", "ajufresh@gmail.com")
    //         .signWith(SignatureAlgorithm.HS256, "secret") // (6)
    //         .compact();
    // }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        
        // OAuth 서비스(github, google, naver)에서 가져온 유저 정보를 담고있음
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // OAuth 서비스 이름(ex. github, naver, google)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth 로그인 시 키(pk)가 되는 값
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 플랫폼 별 oauthUserProfile 추출
        OauthUserProfile oauthUserProfile = OAuthAttributes.extract(registrationId, attributes);
        
        // TODO: 저장여부
        Optional<OauthUserProfile> byOauthId = oauthUserRepository.findByOauthId(oauthUserProfile.getOauthId());
        Users users = null;

        // 조회했는데 없다면 저장
        if (byOauthId.isEmpty()) {
            OauthUserProfile save = oauthUserRepository.save(oauthUserProfile);
            users = saveOrUpdate(save);
        }
        // System.out.println("makeJwtToken입니다: " + makeJwtToken());

        if (byOauthId.isPresent()) {
            users = saveOrUpdate(byOauthId.get());
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(users.getRole().toString())),
                oAuth2User.getAttributes(),
                userNameAttributeName);
    }

    // Oauth2UserProfile -> Users
    private Users saveOrUpdate(OauthUserProfile oauthUserProfile) {

        Users users = usersRepository.findBySnsToken(oauthUserProfile.getOauthId())
                .map(m -> m.update(oauthUserProfile.getName(), oauthUserProfile.getEmail(), oauthUserProfile.getImageUrl()))
                // OAuth 서비스 사이트에서 유저 정보 변경이 있을 수 있기 때문에 우리 DB에도 update, Id는 불변이므로 업데이트 x
                .orElse(oauthUserProfile.toMember(oauthUserProfile.getOauthType(), oauthUserProfile.getOauthId(),
                        oauthUserProfile.getEmail(), oauthUserProfile.getName(), oauthUserProfile.getImageUrl()));

        return usersRepository.save(users);
    }
}

// 전체적인 프로세스
// 구글 로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client라이브러리) -> AccessToken요청
// userRequest 정보 -> loadUser 호출 -> 구글로부터 회원프로필을 받아줌 -> 그 정보를 바탕으로 강제 회원가입

// getClient 정보 (userRequest.getClientRegistration())
// ClientRegistration{registrationId='google', clientId='448219332989-qp4cftqohcm2cv6kln7cseqffbp99nh0.apps.googleusercontent.com', clientSecret='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', clientAuthenticationMethod=org.springframework.security.oauth2.core.ClientAuthenticationMethod@4fcef9d3, authorizationGrantType=org.springframework.security.oauth2.core.AuthorizationGrantType@5da5e9f3, redirectUri='{baseUrl}/{action}/oauth2/code/{registrationId}', scopes=[email, profile], providerDetails=org.springframework.security.oauth2.client.registration.ClientRegistration$ProviderDetails@42ba7b9, clientName='Google'}

// getAccess Token 정보 (userRequest.getAccessToken().getTokenValue())
// org.springframework.security.oauth2.core.OAuth2AccessToken@478c6b27

// 사용자정보 delegate.loadUser(userRequest).getAttributes() 사용자 정보
// {
// sub=115345508245954244066, 
// name=이명학, given_name=이명학, 
// picture=https://lh3.googleusercontent.com/a/AAcHTtcl0k4sQU0nsyiUYMyuet0mPkp3heWxPT1jFUE6=s96-c, 
// email=mhmh77901@gmail.com, email_verified=true, 
// locale=ko
// }