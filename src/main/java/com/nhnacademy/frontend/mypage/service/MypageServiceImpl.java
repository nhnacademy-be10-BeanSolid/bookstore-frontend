package com.nhnacademy.frontend.mypage.service;

import com.nhnacademy.frontend.adapter.user.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final UserAdapter userAdapter;

    @Override
    public boolean withdrawUser(String password) {
        userAdapter.deleteUser();
        return true;
    }
}
