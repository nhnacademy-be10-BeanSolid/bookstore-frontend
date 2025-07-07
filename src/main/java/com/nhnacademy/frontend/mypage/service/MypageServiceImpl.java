package com.nhnacademy.frontend.mypage.service;


import com.nhnacademy.frontend.auth.domain.request.PasswordVerificationRequestDto;
import com.nhnacademy.frontend.common.adapter.AuthAdapter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.domain.response.ResponseUser;
import com.nhnacademy.frontend.mypage.domain.request.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
