package com.nhnacademy.frontend.mypage.service;


import com.nhnacademy.frontend.auth.domain.request.PasswordVerificationRequestDto;
import com.nhnacademy.frontend.common.adapter.AuthAdapter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.domain.response.ResponseAddress;
import com.nhnacademy.frontend.common.adapter.domain.response.ResponsePoint;
import com.nhnacademy.frontend.common.adapter.domain.response.ResponseUser;
import com.nhnacademy.frontend.mypage.domain.request.AddressCreateRequest;
import com.nhnacademy.frontend.mypage.domain.request.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final AuthAdapter authAdapter;
    private final UserAdapter userAdapter;

    @Override
    public boolean withdrawUser(String password) {
        try {
            PasswordVerificationRequestDto verificationRequest = new PasswordVerificationRequestDto(password);

            Boolean isPasswordValid = authAdapter.verifyPassword(verificationRequest);

            if(!isPasswordValid) {
                return false;
            }

            userAdapter.deleteUser();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean withdrawOAuth2User() {
        try {
            userAdapter.deleteUser();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void updatePersonalInformation(UserUpdateRequestDto request) {
        userAdapter.updatePersonalInformation(request);
    }

    @Override
    public ResponseUser getMyInfo() {
        return userAdapter.getUserInfo().getBody();
    }

    @Override
    public List<ResponseAddress> getAllAddresses() {
        return userAdapter.getAllAddresses().getBody();
    }

    @Override
    public void deleteAddress(long addressId) {
        userAdapter.deleteAddress(addressId);
    }

    @Override
    public void addAddress(AddressCreateRequest address) {
        userAdapter.addAddress(address);
    }

    @Override
    public Page<ResponsePoint> getAllPoints(Pageable pageable) {

        return userAdapter.getAllPoints(pageable.getPageNumber(), pageable.getPageSize()).getBody();
    }

    @Override
    public int getUserPoint() {

        ResponseUser responseUser = userAdapter.getUserInfo().getBody();

        return Objects.requireNonNull(responseUser).getUserPoint();
    }


}
