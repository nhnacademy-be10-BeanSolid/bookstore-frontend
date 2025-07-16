package com.nhnacademy.frontend.cart.dto.response;

import com.nhnacademy.frontend.cart.domain.OwnerType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CartCreateResponse {
    private Long cartId;
    private String userId;
    private String guestUUID;
    private OwnerType ownerType;
    private LocalDateTime createdAt;
}
