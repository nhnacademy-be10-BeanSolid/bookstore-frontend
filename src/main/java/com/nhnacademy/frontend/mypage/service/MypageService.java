package com.nhnacademy.frontend.mypage.service;

import com.nhnacademy.frontend.common.adapter.dto.book.response.BookLikeResponse;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseAddress;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePoint;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.common.adapter.dto.user.request.AddressCreateRequest;
import com.nhnacademy.frontend.common.adapter.dto.user.request.UserUpdateRequestDto;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MypageService {
    boolean withdrawUser(String password);

    boolean withdrawOAuth2User();

    void updatePersonalInformation(UserUpdateRequestDto request);

    ResponseUser getMyInfo();

    List<ResponseAddress> getAllAddresses();

    void deleteAddress(long addressId);

    void addAddress(AddressCreateRequest address);

    Page<ResponsePoint> getAllPoints(Pageable pageable);

    int getUserPoint();

    boolean updatePersonalInformationWithPassword(String password);

    ResponsePointType getPointTypeByGradeName(String gradeName);

    void bulkUpdateUserGrades();

    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);

    OrderDetailResponse getOrderDetail(String orderNumber);

    void returnOrder(String orderNumber, String reason, Boolean damaged);

    void cancelOrder(String orderNumber, String reason);

    Page<BookLikeResponse> getBookLikes(Pageable pageable);
}
