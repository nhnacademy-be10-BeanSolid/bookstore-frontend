package com.nhnacademy.frontend.common.service.impl;

import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.common.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAdapter userAdapter;

    @Override
    public Long getCurrentUserPoints() {
        return Optional.ofNullable(userAdapter.getUserInfo())
                .map(ResponseEntity::getBody)
                .map(ResponseUser::getUserPoint)
                .map(Long::valueOf)
                .orElse(0L);
    }

    @Override
    public boolean isDormantUser(String userId){

        return Objects.requireNonNull(userAdapter.getUser(userId).getBody()).getUserStatus().equals("DORMANT");
    }
}
