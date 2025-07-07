package com.nhnacademy.frontend.common.advice;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class GlobalExceptionHandler {

    private int extractStatusCode(String message, int defaultStatus) {
        if (message == null) {
            return defaultStatus;
        }
        Pattern pattern = Pattern.compile("\"status\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        Pattern bracketPattern = Pattern.compile("\\[(\\d{3}) [^\\]]+\\]");
        Matcher bracketMatcher = bracketPattern.matcher(message);
        if (bracketMatcher.find()) {
            return Integer.parseInt(bracketMatcher.group(1));
        }
        return defaultStatus;
    }

    private String getFriendlyMessage(int status) {
        switch (status) {
            case 400: return "잘못된 요청입니다.";
            case 401: return "인증이 필요합니다. 로그인 해주세요.";
            case 403: return "접근 권한이 없습니다.";
            case 404: return "페이지를 찾을 수 없습니다.";
            case 500: return "서버 내부 오류가 발생했습니다.";
            case 502: return "게이트웨이 오류가 발생했습니다.";
            case 503: return "서비스가 일시적으로 이용 불가합니다.";
            default: return "알 수 없는 오류가 발생했습니다.";
        }
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        ResponseStatus responseStatus = e.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;

        String message = e.getMessage();
        int statusCode = extractStatusCode(message, status.value());
        String userFriendlyMessage = getFriendlyMessage(statusCode);

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("userFriendlyMessage", userFriendlyMessage); // 추가

        return "error/error";
    }
}
