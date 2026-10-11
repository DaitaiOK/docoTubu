package com.example.docotubu.config;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * CSRF対策用のトークンをセッションごとに発行する
 * ※各画面のフォームは、このトークンを hidden の「_csrf」パラメーターで送信する（照合は CsrfInterceptor）
 * ※index.jsp はコントローラーを通らずに表示されることがあるため、インターセプターではなくフィルターで発行する
 */
@Component
public class CsrfTokenFilter extends OncePerRequestFilter {

    // セッションスコープの属性名（JSPでは ${sessionScope.csrfToken} で参照する）
    public static final String SESSION_KEY = "csrfToken";
    // フォームで送信するパラメーター名
    public static final String PARAM_NAME = "_csrf";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute(SESSION_KEY) == null) {
            session.setAttribute(SESSION_KEY, generateToken());
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // CSSなどの静的ファイルではセッションを作らない
        return request.getServletPath().startsWith("/css/");
    }

    /**
     * トークンを作り直す（ログイン時に呼ぶ）
     * ※ログイン前のトークンを知っている第三者に、ログイン後の操作を偽造されないようにするため
     */
    public static void regenerateToken(HttpSession session) {
        session.setAttribute(SESSION_KEY, generateToken());
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
