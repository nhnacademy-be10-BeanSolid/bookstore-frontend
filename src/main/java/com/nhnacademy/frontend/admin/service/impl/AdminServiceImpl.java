package com.nhnacademy.frontend.admin.service.impl;


import com.nhnacademy.frontend.admin.adapter.UserAdminAdapter;
import com.nhnacademy.frontend.admin.service.AdminService;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserAdminAdapter userAdminAdapter;

    @Override
    public Page<ResponsePointType> getAllPointTypes(Pageable pageable) {
        return userAdminAdapter.getAllPointTypes(pageable.getPageNumber(), pageable.getPageSize()).getBody();
    }

    @Override
    public void addPointType(PointTypeCreateRequestDto requestDto) {

        userAdminAdapter.addPointType(requestDto);
    }

    @Override
    public void deletePointType(Long typeId) {

        userAdminAdapter.deletePointType(typeId);
    }

    @Override
    public void changeActive(Long pointTypeId) {

        userAdminAdapter.changeIsActivePointType(pointTypeId);
    }

    @Override
    public void updatePointType(Long pointTypeId, PointTypeUpdateRequestDto requestDto) {

        userAdminAdapter.editPointType(requestDto, pointTypeId);
    }

    @Override
    public ResponsePointType getPointType(Long pointTypeId) {

        return userAdminAdapter.getPointType(pointTypeId).getBody();
    }

    @Override
    public Page<ResponseUser> getAllUsers(Pageable pageable) {

        return userAdminAdapter.getAllUsers(pageable.getPageNumber(), pageable.getPageSize()).getBody();
    }

    @Override
    public void bulkUpdate() {

        userAdminAdapter.bulkUpdateUserStatus();
    }


}
