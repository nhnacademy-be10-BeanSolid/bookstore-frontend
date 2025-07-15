package com.nhnacademy.frontend.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuestCreateRequest(@NotBlank @Size(max = 255) String guestPassword,
                                 Long orderId
                                  ){
}
