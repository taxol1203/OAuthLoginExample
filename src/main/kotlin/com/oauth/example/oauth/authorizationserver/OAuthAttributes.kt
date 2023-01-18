package com.oauth.example.oauth.authorizationserver

data class OAuthAttributes(
    val attributes: Map<String, Any>,
    val nameAttributeKey: String,
    val name: String,
    val email: String,
) {
    companion object {
        fun of(
            registrationId: String,
            userNameAttributeName: String,
            attributes: Map<String, Any>
        ): OAuthAttributes {
            return OAuthAttributes(
                name = attributes["name"] as String,
                email = attributes["email"] as String,
                attributes = attributes,
                nameAttributeKey = userNameAttributeName
            )
        }
    }
}

// User Entity 생성, 즉 회원가입에 사용될 것이다.
fun OAuthAttributes.toEntity(): Account {
    return Account(
        name = name,
        email = email,
        role = Role.USER
    )
}