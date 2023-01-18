package com.oauth.example.oauth.authorizationserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.web.filter.CharacterEncodingFilter

/**
 * 스프링 시큐리티 관련 설정입니다.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Autowired private val customOAuth2AccountService: CustomOAuth2AccountService
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {

        http
            // OAuth2 적용 관련 설정입니다.
            .addFilterAt(CharacterEncodingFilter(), CsrfFilter::class.java)
            .csrf().disable()
            // URI 접근과 관련된 설정입니다.
            .authorizeRequests()
            // 아래 url으로만 접근 가능하도록 세팅
            .antMatchers("/", "/login/**", "/oauth2/**", "/images/**", "/api/login/**").permitAll()
            .anyRequest().authenticated()

            // Iframe 사용 허용합니다.
            .and()
            .headers().frameOptions().disable()

            // 인증되지 않은 사용자를 원하는 페이지로 이동시킵니다.
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login"))

            // 로그인 인증 후 이동 페이지 설정입니다.
            .and()
            .formLogin()
            .successForwardUrl("/welcome")

            // 로그아웃과 관련한 설정입니다.
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)

            .and()
            .oauth2Login()
            .defaultSuccessUrl("/login/complelte")
            .userInfoEndpoint() // userInfo Endpoint, 즉 로그인 성공 후에 관하여 설정
            .userService(customOAuth2AccountService) // 로그인 성공후에 사용할 Service 등록

    }

    /**
     * OAuth2 설정입니다.
     */
    @Bean
    fun clientRegistrationRepository(oAuth2ClientProperties: OAuth2ClientProperties,
    ): InMemoryClientRegistrationRepository {

        // 소셜 설정 등록
        val registrations = oAuth2ClientProperties.registration.keys
            .map { getRegistration(oAuth2ClientProperties, it) }
            .filter { it != null }
            .toMutableList()

        registrations.add(
            CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId("put client id")   // 카카오 oauth의 client id (restful id)
                .clientSecret("put secret") // 카카오 oauth의 secret pw
                .jwkSetUri("temp")
                .build());

        return InMemoryClientRegistrationRepository(registrations)
    }

    // 공통 소셜 설정을 호출합니다.
    private fun getRegistration(clientProperties: OAuth2ClientProperties, client: String): ClientRegistration? {
        val registration = clientProperties.registration[client]
        return when(client) {
            "google" -> CommonOAuth2Provider.GOOGLE.getBuilder(client)
                .clientId(registration?.clientId)
                .clientSecret(registration?.clientSecret)
                .scope("email", "profile")
                .build()
            else -> null
        }
    }
}