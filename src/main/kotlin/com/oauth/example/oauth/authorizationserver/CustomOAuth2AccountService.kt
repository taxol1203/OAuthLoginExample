package com.oauth.example.oauth.authorizationserver

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import javax.servlet.http.HttpSession

@Service
class CustomOAuth2AccountService(private val accountRepository: AccountRepository,
                                 private val httpSession: HttpSession
): OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        if (userRequest == null) throw OAuth2AuthenticationException("Error")
        println("hi")
        // registrationId는 로그인 진행중인 서비스 코드
        // 구글, 카카오등을 구분
        val registrationId = userRequest.clientRegistration.registrationId

        //if(registrationId == "google"){
        val delegate = DefaultOAuth2UserService()
        // 받은 access token으로 user 정보를 요청하여 받아온다.
        val oAuth2User = delegate.loadUser(userRequest)

        // OAuth2 로그인 진행시 키가 되는 필드값
        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName

        // OAuth2User의 attribute가 된다.
        // 추후 다른 소셜 로그인도 이 클래스를 쓰게 될 것이다.
        lateinit var attributes: OAuthAttributes
        // 구글의 경우
        if(registrationId == "google"){
            attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.attributes
            )
        }
        else{   // 카카오의 경우
            val kakaoUserInfo = mutableMapOf<String, Any>()
            val kakaoMap = oAuth2User.attributes["kakao_account"] as Map<*, *>

            val kakapProfile = kakaoMap["profile"] as Map<*, *>
            kakapProfile["nickname"]?.let { kakaoUserInfo.put("name", it) }
            kakaoMap["email"]?.let {kakaoUserInfo.put("email", it)}
            oAuth2User.attributes["id"]?.let { kakaoUserInfo.put("id", it) }

            attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                kakaoUserInfo
            )
        }

        // 전달받은 OAuth2User의 attribute를 이용하여 회원가입 및 수정의 역할을 한다.
        // User Entity 생성 : 회원가입
        // User Entity 수정 : update
        val user = saveOrUpdate(attributes)
        println(user)
        // session에 SessionUser(user의 정보를 담는 객체)를 담아 저장한다.
        httpSession.setAttribute("user", SessionUser(user))

        return DefaultOAuth2User(
            setOf(SimpleGrantedAuthority(user.role.key)),
            attributes.attributes,
            attributes.nameAttributeKey
        )
    }

    fun saveOrUpdate(attributes: OAuthAttributes): Account {
        val user = accountRepository.findByEmail(attributes.email)
            ?.copy(name = attributes.name)
            ?: attributes.toEntity()

        return accountRepository.save(user)
    }
}