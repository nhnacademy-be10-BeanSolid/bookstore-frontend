package com.nhnacademy.frontend.mypage.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressCreateRequest(
     @NotBlank @Size(max = 30) String addressNickName,
     @NotBlank @Size(max = 255) String addressDetail,
     @NotBlank @Size(max = 20) String userId){
}
