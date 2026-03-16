package com.haile.springrestfulapi.config;

import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        OAuth2User user = super.loadUser(request);

        String email = user.getAttribute("email");

        UserEntity userEntity = userService.findOrCreateNewUser(email);

        Map<String, Object> attributes = new HashMap<>(user.getAttributes());
        
        attributes.put("userEntity", userEntity);

        return new DefaultOAuth2User(user.getAuthorities(), attributes, "email");
    }
}