package com.nhnacademy.frontend.util;

import lombok.Getter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Getter
public class ErrorMessage {
    private final String timestamp;
    private final int status;
    private final String error;
    private final String path;
    private final String message;

    public ErrorMessage(int status, String error, String path, String message) {
        this.timestamp = ZonedDateTime.now(ZoneOffset.UTC).toString();
        this.status = status;
        this.error = error;
        this.path = path;
        this.message = message;
    }
}