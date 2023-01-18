package com.oauth.example.oauth.authorizationserver

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@Controller
class LoginController(
    private val httpSession: HttpSession
) {
    // index페이지 호출 시 로그인 페이지로 이동
    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("hi", "hi")
        val user = httpSession.getAttribute("user") as SessionUser?

        // 세션정보가 이미 있을 시
        if (user != null) {
            model.addAttribute("user", user)    // 세션에 저장된 user 정보를 view에 전달한다.
            return "redirect:/welcome"
        }
        println("hello everyone")
        return "redirect:/login"
    }

    // 로그인 페이지
    @GetMapping("/login")
    fun login() = "login"

    // 로그인 성공 URI
    @GetMapping("/login/complelte")
    fun loginComplete() = "redirect:/welcome"

    // 로그인 후 웰컴 페이지 (인증 후 접근 가능)
    @GetMapping("/welcome")
    @ResponseBody
    fun welcome() = "Hello! Social Login!!"
}