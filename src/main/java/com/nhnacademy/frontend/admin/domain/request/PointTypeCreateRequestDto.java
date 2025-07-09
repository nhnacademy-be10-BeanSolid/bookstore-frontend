package com.nhnacademy.frontend.admin.domain.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PointTypeCreateRequestDto (@NotBlank @Size(max = 20) String typeName,
                                      @Min(0) int earningPoint,
                                        @Min(0) int earningRate,
                                      @Size(max = 10) String gradeName,
                                      @NotNull Boolean isActive){
}


