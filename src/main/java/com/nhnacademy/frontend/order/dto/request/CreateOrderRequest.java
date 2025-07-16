package com.nhnacademy.frontend.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotEmpty(message = "구매할 상품을 추가해주세요(현재: 구매할 상품 없음)")
    @Valid
    private List<CreateOrderItemRequest> createItemRequests;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderItemRequest {

        @NotNull
        @Positive
        private Long bookId;

        @NotNull
        @Positive
        private Integer quantity;
    }
}
