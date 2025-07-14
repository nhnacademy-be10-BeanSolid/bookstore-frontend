package com.nhnacademy.frontend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayType {

    CARD("카드"),
    ACCOUNT("계좌");

    private final String description;
}
