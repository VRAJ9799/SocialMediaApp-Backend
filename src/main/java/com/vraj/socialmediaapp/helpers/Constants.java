package com.vraj.socialmediaapp.helpers;

public class Constants {
    //region Role
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    //endregion

    //region Swagger
    public static final String TITLE = "Social Media App";
    public static final String VERSION = "1.0";
    public static final String NAME = "VRAJ SHAH";
    public static final String EMAIL = "vrajshah363@gmail.com";
    public static final String SECURITY_NAME = "JWT Authentication";
    public static final String SECURITY_SCHEME = "bearer";
    //endregion

    public static final String AUTHORIZATION = "Authorization";

    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String[] WHITELISTED_URLS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/auth/**",
    };
    public static final String[] ADMIN_URLS = {
            "/roles/**",
            "/admin/**"
    };
}
