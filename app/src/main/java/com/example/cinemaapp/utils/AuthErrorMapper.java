package com.example.cinemaapp.utils;

public final class AuthErrorMapper {
    private AuthErrorMapper() {
    }

    public static String toUserMessage(Exception e) {
        if (e == null || e.getMessage() == null) {
            return "Xac thuc that bai";
        }

        String raw = e.getMessage();
        String lower = raw.toLowerCase();

        if (lower.contains("configuration_not_found")) {
            return "Firebase chua cau hinh dung cho app nay. Kiem tra package, SHA-1/SHA-256 va tai lai google-services.json.";
        }
        if (lower.contains("operation_not_allowed")) {
            return "Email/Password chua duoc bat trong Firebase Authentication.";
        }
        if (lower.contains("network") || lower.contains("timeout")) {
            return "Loi ket noi mang, vui long thu lai.";
        }

        return raw;
    }
}
