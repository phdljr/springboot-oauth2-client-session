package com.phdljr.springbootoauth2clientsession.service;

import com.phdljr.springbootoauth2clientsession.dto.CustomOAuth2User;
import com.phdljr.springbootoauth2clientsession.dto.GoogleResponse;
import com.phdljr.springbootoauth2clientsession.dto.NaverResponse;
import com.phdljr.springbootoauth2clientsession.dto.OAuth2Response;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

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

        String role = "ROLE_USER";
        return new CustomOAuth2User(oAuth2Response, role);
    }
}
