package com.nhnacademy.frontend.common.adapter.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuestCreateRequest(@NotBlank @Size(max = 255) String guestPassword,
                                 Long orderId
                                  ){
}
