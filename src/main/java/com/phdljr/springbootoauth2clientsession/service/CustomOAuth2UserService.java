package com.phdljr.springbootoauth2clientsession.service;

import com.phdljr.springbootoauth2clientsession.dto.CustomOAuth2User;
import com.phdljr.springbootoauth2clientsession.dto.GoogleResponse;
import com.phdljr.springbootoauth2clientsession.dto.NaverResponse;
import com.phdljr.springbootoauth2clientsession.dto.OAuth2Response;
import com.phdljr.springbootoauth2clientsession.entity.UserEntity;
import com.phdljr.springbootoauth2clientsession.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // 해당 메소드에 naver나 google 등 로그인한 정보가 userRequest에 담겨옴
    @Override
    public OAuth2User loadUser(
        final OAuth2UserRequest userRequest
    ) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        UserEntity existData = userRepository.findByUsername(username)
            .orElse(null);

        String role = "ROLE_USER";

        // 새로운 사용자라면 데이터 생성
        if (existData == null) {

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setRole(role);

            userRepository.save(userEntity);
        } else {

            existData.setUsername(username);
            existData.setEmail(oAuth2Response.getEmail());

            role = existData.getRole();

            userRepository.save(existData);
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}
