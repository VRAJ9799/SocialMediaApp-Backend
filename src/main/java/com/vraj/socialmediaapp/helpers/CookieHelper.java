package com.vraj.socialmediaapp.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
public class CookieHelper {

    @Value("${cookie.secure}")
    private boolean isSecure;
    @Value("${cookie.http_only}")
    private boolean isHttpOnly;

    public void addCookie(HttpServletResponse httpServletResponse, String name, String value, int expireIn) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setSecure(isSecure);
        cookie.setHttpOnly(isHttpOnly);
        if (expireIn != -1) cookie.setMaxAge(expireIn);
        httpServletResponse.addCookie(cookie);
    }

    public String getCookie(HttpServletRequest httpServletRequest, String name) {
        String value = Arrays.stream(httpServletRequest.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        return value;
    }
}
