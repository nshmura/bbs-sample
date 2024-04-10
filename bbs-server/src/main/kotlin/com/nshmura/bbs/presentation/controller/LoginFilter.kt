package com.nshmura.bbs.presentation.controller

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class LoginFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI.matches("^/$".toRegex())) { // トップページは認証不要
            filterChain.doFilter(request, response)
            return
        }

        val accessToken = request.cookies.firstOrNull { it.name == "accessToken" }?.value
        if (accessToken == null) {
            response.sendRedirect("/") // トップページにリダイレクトしてユーザを選択させる
            return
        }

        // TODO: 簡単のため accessToken に userId をセットしている。本来は accessToken の検証や userId の取得が必要
        request.session.setAttribute("userId", accessToken)
        filterChain.doFilter(request, response)
    }
}
