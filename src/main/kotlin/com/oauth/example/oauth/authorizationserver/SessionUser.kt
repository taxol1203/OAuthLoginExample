package com.oauth.example.oauth.authorizationserver

import java.io.Serializable

data class SessionUser(
    private val account: Account
): Serializable {
    val name = account.name
    val email = account.email
}