package com.auth.app.utils;

import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {

    public static IpAddress extractIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return new IpAddress(ip);
    }

    public static UserAgent extractUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return new UserAgent(ua);
    }
}
