package com.nhnacademy.frontend.order.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nhnacademy.frontend.payment.dto.response.PaymentResponseDto;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = OrderStatusResult.CancelResult.class, name = "cancel"),
    @JsonSubTypes.Type(value = OrderStatusResult.ReturnResult.class, name = "return")
})
public sealed interface OrderStatusResult
        permits OrderStatusResult.CancelResult, OrderStatusResult.ReturnResult {

    record CancelResult(PaymentResponseDto payment) implements OrderStatusResult {}
    record ReturnResult(OrderResponse order) implements OrderStatusResult {}
}
