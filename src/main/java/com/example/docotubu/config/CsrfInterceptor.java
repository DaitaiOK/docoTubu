package com.example.docotubu.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * POSTリクエストのCSRFトークンを照合し、一致しない場合は処理を実行せずに画面へ戻す
 * ※状態を変更する処理（投稿・編集・削除・ログイン・ログアウト・ユーザー登録）はすべてPOSTで受け付ける
 */
@Component
public class CsrfInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        String expected = (session == null) ? null : (String) session.getAttribute(CsrfTokenFilter.SESSION_KEY);
        String actual = request.getParameter(CsrfTokenFilter.PARAM_NAME);
        if (expected != null && actual != null
                && MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                        actual.getBytes(StandardCharsets.UTF_8))) {
            return true;
        }

        // セッション切れで画面を開き直していない場合もここに来るため、メッセージを添えて画面を開き直してもらう
        System.out.println("[CsrfInterceptor] CSRFトークンが一致しないため処理を中止しました: " + request.getRequestURI());
        boolean loggedIn = session != null && session.getAttribute("loginUser") != null;
        String location = request.getContextPath() + (loggedIn ? "/Main" : "/");

        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put("errorMsg", "画面の有効期限が切れたか、不正なリクエストです。もう一度操作してください");
        RequestContextUtils.saveOutputFlashMap(location, request, response);
        response.sendRedirect(location);
        return false;
    }
}
