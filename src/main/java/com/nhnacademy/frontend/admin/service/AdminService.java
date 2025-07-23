package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    Page<ResponsePointType> getAllPointTypes(Pageable pageable);

    void addPointType(PointTypeCreateRequestDto requestDto);

    void deletePointType(Long pointTypeId);

    void changeActive(Long pointTypeId);

    void updatePointType(Long pointTypeId, @Valid PointTypeUpdateRequestDto requestDto);

    ResponsePointType getPointType(Long pointTypeId);

    Page<ResponseUser> getAllUsers(Pageable pageable);

    void bulkUpdate();
}
