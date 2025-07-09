package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.admin.domain.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.ResponsePointType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

public interface AdminService {

    Page<ResponsePointType> getAllPointTypes(Pageable pageable);

    void addPointType(PointTypeCreateRequestDto requestDto);

    void deletePointType(Long pointTypeId);

    void changeActive(Long pointTypeId);

    void updatePointType(Long pointTypeId, @Valid PointTypeUpdateRequestDto requestDto);

    ResponsePointType getPointType(Long pointTypeId);
}
