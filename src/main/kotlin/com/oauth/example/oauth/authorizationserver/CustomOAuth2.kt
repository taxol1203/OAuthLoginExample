package com.oauth.example.oauth.authorizationserver

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod

/**
 * 커스텀 소셜 Provider
 */
enum class CustomOAuth2Provider {
    KAKAO {
        override fun getBuilder(registrationId: String) =
            getBuilder(registrationId, ClientAuthenticationMethod.POST, DEFAULT_LOGIN_REDIRECT_URL)
                .scope("account_email", "profile_nickname")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")       // 발급받은 access token으로 user 정보를 받기위한 url
                .userNameAttributeName("id")
                .clientName("Kakao")
    },
    NAVER {     // 네이버는 사용 x
        override fun getBuilder(registrationId: String) =
            getBuilder(registrationId, ClientAuthenticationMethod.POST, DEFAULT_LOGIN_REDIRECT_URL)
                .scope("profile")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .clientName("Naver")
    };

    companion object {
        val DEFAULT_LOGIN_REDIRECT_URL = "{baseUrl}/login/oauth2/code/{registrationId}"
    }

    protected fun getBuilder(registrationId: String, method: ClientAuthenticationMethod, redirectUri: String) =
        ClientRegistration.withRegistrationId(registrationId)
            .clientAuthenticationMethod(method)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUriTemplate(redirectUri)

    abstract fun getBuilder(registrationId: String): ClientRegistration.Builder
}