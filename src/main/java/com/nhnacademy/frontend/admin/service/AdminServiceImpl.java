package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.admin.domain.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.ResponsePointType;
import com.nhnacademy.frontend.admin.adapter.AdminAdapter;
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


}
