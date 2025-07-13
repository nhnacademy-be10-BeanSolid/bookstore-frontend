package com.nhnacademy.frontend.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UpdateOrderRequest {

    @NotEmpty(message = "구매할 상품을 추가해주세요(현재: 구매할 상품 없음)")
    @Valid
    private List<WrappingRequest> wrappingRequests;

    @NotBlank(message = "배송 받을 사람을 적어주세요")
    private String receiverName;

    @NotBlank(message = "배송 받을 사람 전화번호를 입력해주세요")
    @Pattern(regexp = "^01\\d-\\d{4}-\\d{4}$",
            message = "올바른 휴대폰 번호 형식이 아닙니다 (올바른 형식: 010-1234-5678)")
    private String receiverPhoneNumber;

    @NotBlank(message = "배송지를 선택해주세요")
    private String address;

    @Future(message = "배송 요청 날짜는 주문일 다음 날부터 가능합니다")
    private LocalDate requestedDeliveryDate;

    @Setter
    @Getter
    public static class WrappingRequest {

        @NotNull @Positive
        private Long bookId;

        @Positive
        private Long wrappingId;
    }
}
