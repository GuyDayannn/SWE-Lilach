package org.cshaifa.spring.utils;

public class Constants {
    // Messages
    public static final String DATABASE_ERROR = "database_error";
    public static final String SUCCESS_MSG = "Success";
    public static final String FAIL_MSG = "Fail";
    public static final String CUSTOMER_FROZEN_MSG = "Your account is frozen. Contact System Admin";
    public static final String WRONG_CREDENTIALS = "The username or password is incorrect";
    public static final String EMAIL_EXISTS = "A user with this email already exists";
    public static final String USERNAME_EXISTS = "A user with this username already exists";

    public static final int SERVER_PORT = 8095;
    public static final long REQUEST_TIMEOUT = 5;
    public static final long LOADING_TIMEOUT = REQUEST_TIMEOUT;

    public static final int PASSWORD_SALT_SIZE = 24;
    public static final int PASSWORD_KEY_LENGTH = 512;
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512";
    public static final int PBKDF2_ITERATIONS = 10000;
}
