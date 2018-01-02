package com.apns;

import java.nio.charset.Charset;

public class Util {

    static final Charset UTF_8 = Charset.forName("UTF-8");
    static final String ENDPOINT_PRODUCTION = "https://api.push.apple.com/3/device/";
    static final String ENDPOINT_SANDBOX = "https://api.development.push.apple.com/3/device/";

    public static String sanitizeTokenString(final String tokenString) {
        return tokenString.replaceAll("[^a-fA-F0-9]", "");
    }
}
