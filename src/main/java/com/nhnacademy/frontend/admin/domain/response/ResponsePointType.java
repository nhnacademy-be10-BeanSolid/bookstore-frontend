package com.nhnacademy.frontend.admin.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePointType {

    private Long typeId;
    private String typeName;
    private int earningPoint;
    private int earningRate;
    private String gradeName;
    private Boolean isActive;
}
