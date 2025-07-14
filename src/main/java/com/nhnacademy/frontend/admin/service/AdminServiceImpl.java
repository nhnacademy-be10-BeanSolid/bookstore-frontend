package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.admin.adapter.AdminAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminAdapter adminAdapter;

    @Override
    public Page<ResponsePointType> getAllPointTypes(Pageable pageable) {
        return adminAdapter.getAllPointTypes(pageable.getPageNumber(), pageable.getPageSize()).getBody();
    }

    @Override
    public void addPointType(PointTypeCreateRequestDto requestDto) {

        adminAdapter.addPointType(requestDto);
    }

    @Override
    public void deletePointType(Long typeId) {

        adminAdapter.deletePointType(typeId);
    }

    @Override
    public void changeActive(Long pointTypeId) {

        adminAdapter.changeIsActivePointType(pointTypeId);
    }

    @Override
    public void updatePointType(Long pointTypeId, PointTypeUpdateRequestDto requestDto) {

        adminAdapter.editPointType(requestDto, pointTypeId);
    }

    @Override
    public ResponsePointType getPointType(Long pointTypeId) {

        return adminAdapter.getPointType(pointTypeId).getBody();
    }


}
