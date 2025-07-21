package com.nhnacademy.frontend.auth.exception;

public class UserDormantException extends RuntimeException {
    public UserDormantException(String message) {
        super(message);
    }
}
