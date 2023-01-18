package com.oauth.example.oauth.authorizationserver

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Account(
    @Id
    @GeneratedValue
    val id: Long = 0,

    @Column(unique = true)
    val email: String,
    val name: String,

    @Enumerated(EnumType.STRING)
    val role: Role,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class Role(
    val key: String,
    val title: String
) {
    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "사용자")
}