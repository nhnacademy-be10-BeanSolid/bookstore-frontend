package com.nhnacademy.frontend.common.adapter.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePointType {

    private Long typeId;
    private String typeName;
    private Integer earningPoint;
    private Integer earningRate;
    private String gradeName;
}
