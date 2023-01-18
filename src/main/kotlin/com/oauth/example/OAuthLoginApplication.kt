package com.oauth.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync


@SpringBootApplication
@EnableAsync
class OAuthLoginApplication

fun main(args: Array<String>) {
	runApplication<OAuthLoginApplication>(*args)
}
