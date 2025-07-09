package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.admin.domain.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.ResponsePointType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

public interface AdminService {

    Page<ResponsePointType> getAllPointTypes(Pageable pageable);

    void addPointType(PointTypeCreateRequestDto requestDto);

    void deletePointType(Long pointTypeId);
}
