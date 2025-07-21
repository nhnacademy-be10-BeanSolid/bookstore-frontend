package com.nhnacademy.frontend.mypage.service;

import com.nhnacademy.frontend.auth.adapter.AuthAdapter;
import com.nhnacademy.frontend.auth.dto.request.PasswordVerificationRequestDto;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.request.AddressCreateRequest;
import com.nhnacademy.frontend.common.adapter.dto.user.request.UserUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseAddress;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePoint;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.mypage.service.impl.MypageServiceImpl;
import com.nhnacademy.frontend.order.adapter.OrderAdapter;
import com.nhnacademy.frontend.order.dto.request.ReturnsRequest;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MypageServiceImpl 단위 테스트")
class MypageServiceImplTest {
    @Mock
    private AuthAdapter authAdapter;
    @Mock
    private UserAdapter userAdapter;
    @Mock
    private OrderAdapter orderAdapter;

    @InjectMocks
    private MypageServiceImpl mypageService;

    private UserUpdateRequestDto userUpdateRequestDto;
    private AddressCreateRequest addressCreateRequest;
    private ResponseUser responseUser;
    private ResponseAddress responseAddress;
    private PasswordVerificationRequestDto passwordVerificationRequestDto;
    private ResponsePointType responsePointType;
    private OrderSummaryResponse orderSummaryResponse;
    private OrderDetailResponse orderDetailResponse;

    @BeforeEach
    void setUp() {
        userUpdateRequestDto = createUserUpdateRequestDto();
        addressCreateRequest = createAddressCreateRequest();
        responseUser = createResponseUser();
        responseAddress = createResponseAddress();
        passwordVerificationRequestDto = new PasswordVerificationRequestDto("validPassword");
        responsePointType = createResponsePointType();
        orderSummaryResponse = createOrderSummaryResponse();
        orderDetailResponse = createOrderDetailResponse();
    }

    private UserUpdateRequestDto createUserUpdateRequestDto() {
        return new UserUpdateRequestDto("12345", "홍길동", "010-1234-5678", "test@test.test", LocalDate.of(1990, 1, 1));
    }

    private AddressCreateRequest createAddressCreateRequest() {
        return new AddressCreateRequest("테스트집", "이건 테스트용 자세한 주소", "testUserId");
    }

    private ResponseUser createResponseUser() {
        return new ResponseUser(
                1L, "Test User", "asdfghjkl", "test", "010-1111-1111", "asdf@asdf.asdf", LocalDate.now(), 1000, false, null, null,null, null);
    }

    private ResponseAddress createResponseAddress() {
        return new ResponseAddress(
                1L, "테스트집", "이건 테스트용 자세한 주소", "testUserId");
    }

    private ResponsePointType createResponsePointType() {
        return new ResponsePointType(1L, "테스트 포인트 타입", null, 2, "BASIC", true);
    }

    private OrderSummaryResponse createOrderSummaryResponse() {
        return new OrderSummaryResponse(LocalDate.now(), "202507-abcdef-123456", "받는 사람", 30000L, "PENDING");
    }

    private OrderDetailResponse createOrderDetailResponse() {
        return OrderDetailResponse.builder()
                .orderNumber("202507-abcdef-123456")
                .totalAmount(30_000L)
                .build();
    }


    @Test
    @DisplayName("회원 탈퇴 - 성공")
    void withdrawUser_Success() {
        String password = "validPassword";
        when(authAdapter.verifyPassword(any())).thenReturn(true);

        boolean result = mypageService.withdrawUser(password);

        assertTrue(result);
        verify(userAdapter).deleteUser();
    }

    @Test
    @DisplayName("회원 탈퇴 - 실패 (비밀번호 불일치)")
    void withdrawUser_Failure() {
        String password = "invalidPassword";
        when(authAdapter.verifyPassword(any())).thenReturn(false);

        boolean result = mypageService.withdrawUser(password);

        assertFalse(result);
    }

    @Test
    @DisplayName("회원 탈퇴 - 실패 (예외 발생)")
    void withdrawUser_Exception() {
        String password = "anyPassword";
        when(authAdapter.verifyPassword(any())).thenThrow(new RuntimeException("Service error"));

        boolean result = mypageService.withdrawUser(password);

        assertFalse(result);
    }

    @Test
    @DisplayName("회원 탈퇴 - 성공(OAuth2 사용자)")
    void withdrawOAuth2User_Success() {
        when(userAdapter.deleteUser()).thenReturn(null);

        boolean result = mypageService.withdrawOAuth2User();

        assertTrue(result);
        verify(userAdapter).deleteUser();
    }

    @Test
    @DisplayName("회원 탈퇴 - 실패(OAuth2 사용자, 예외 발생)")
    void withdrawOAuth2User_Exception() {
        when(userAdapter.deleteUser()).thenThrow(new RuntimeException("Service error"));

        boolean result = mypageService.withdrawOAuth2User();

        assertFalse(result);
    }

    @Test
    @DisplayName("개인 정보 수정 - 성공")
    void updatePersonalInformation_Success() {

        mypageService.updatePersonalInformation(userUpdateRequestDto);

        verify(userAdapter).updatePersonalInformation(userUpdateRequestDto);
    }

    @Test
    @DisplayName("내 정보 조회 - 성공")
    void getMyInfo_Success() {
        when(userAdapter.getUserInfo()).thenReturn(ResponseEntity.ok(responseUser));
        mypageService.getMyInfo();

        verify(userAdapter).getUserInfo();
    }

    @Test
    @DisplayName("모든 주소 조회 - 성공")
    void getAllAddresses_Success() {
        when(userAdapter.getAllAddresses()).thenReturn(ResponseEntity.ok(List.of(responseAddress)));

        List<ResponseAddress> addresses = mypageService.getAllAddresses();

        assertFalse(addresses.isEmpty());
        verify(userAdapter).getAllAddresses();
    }

    @Test
    @DisplayName("주소 삭제 - 성공")
    void deleteAddress_Success() {
        long addressId = 1L;

        mypageService.deleteAddress(addressId);

        verify(userAdapter).deleteAddress(addressId);
    }

    @Test
    @DisplayName("주소 추가 - 성공")
    void addAddress_Success() {
        mypageService.addAddress(addressCreateRequest);

        verify(userAdapter).addAddress(addressCreateRequest);
    }

    @Test
    @DisplayName("모든 포인트 조회 - 성공")
    void getAllPoints_Success() {
        when(userAdapter.getAllPoints(0, 10)).thenReturn(ResponseEntity.ok(Page.empty()));

        Page<ResponsePoint> points = mypageService.getAllPoints(Pageable.ofSize(10));

        assertTrue(points.isEmpty());
        verify(userAdapter).getAllPoints(0, 10);
    }

    @Test
    @DisplayName("내 포인트 조회 - 성공")
    void getUserPoint_Success() {
        when(userAdapter.getUserInfo()).thenReturn(ResponseEntity.ok(responseUser));

        int userPoint = mypageService.getUserPoint();

        assertTrue(userPoint >= 0);
        verify(userAdapter).getUserInfo();
    }

    @Test
    @DisplayName("정보 수정을 위한 비밀번호 검증 - 성공")
    void verifyPasswordForUpdate_Success() {

        when(authAdapter.verifyPassword(passwordVerificationRequestDto)).thenReturn(true);

        boolean result = mypageService.updatePersonalInformationWithPassword(passwordVerificationRequestDto.password());

        assertTrue(result);
        verify(authAdapter).verifyPassword(passwordVerificationRequestDto);
    }

    @Test
    @DisplayName("정보 수정을 위한 비밀번호 검증 - 실패")
    void verifyPasswordForUpdate_Failure() {

        when(authAdapter.verifyPassword(passwordVerificationRequestDto)).thenReturn(false);

        boolean result = mypageService.updatePersonalInformationWithPassword(passwordVerificationRequestDto.password());

        assertFalse(result);
        verify(authAdapter).verifyPassword(passwordVerificationRequestDto);
    }

    @Test
    @DisplayName("정보 수정을 위한 비밀번호 검증 - 예외 발생")
    void verifyPasswordForUpdate_Exception() {
        when(authAdapter.verifyPassword(passwordVerificationRequestDto)).thenThrow(new RuntimeException("Service error"));

        boolean result = mypageService.updatePersonalInformationWithPassword(passwordVerificationRequestDto.password());

        assertFalse(result);
        verify(authAdapter).verifyPassword(passwordVerificationRequestDto);
    }

    @Test
    @DisplayName("회원등급에 따른 포인트 타입 조회 - 성공")
    void getPointTypeByUserGrade_Success() {
        Page<ResponsePointType> page;
        page = new org.springframework.data.domain.PageImpl<>(List.of(responsePointType));
        when(userAdapter.getPointTypeByGradeName("BASIC", null)).thenReturn(ResponseEntity.ok(page));

        ResponsePointType pointType = mypageService.getPointTypeByGradeName("BASIC");

        assertEquals("BASIC", pointType.getGradeName());
        verify(userAdapter).getPointTypeByGradeName("BASIC", null);
    }

    @Test
    @DisplayName("회원등급 갱신 - 성공")
    void updateUserGrade_Success() {

        when(userAdapter.bulkUpdateUserGrades()).thenReturn(ResponseEntity.ok().build());

        mypageService.bulkUpdateUserGrades();
        verify(userAdapter).bulkUpdateUserGrades();
    }

    @Test
    @DisplayName("주문 목록 조회 - 성공")
    void getAllOrders_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderSummaryResponse> orderPage = new PageImpl<>(List.of(orderSummaryResponse), pageable, 1);

        when(orderAdapter.getAllOrdersByUserId(pageable)).thenReturn(orderPage);

        Page<OrderSummaryResponse> result = mypageService.getAllOrders(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("202507-abcdef-123456", result.getContent().getFirst().orderNumber());
        verify(orderAdapter).getAllOrdersByUserId(pageable);
    }

    @Test
    @DisplayName("주문 상세 조회 - 성공")
    void getOrderDetail_Success() {
        String orderNumber = "202507-abcdef-123456";

        when(orderAdapter.getOrder(orderNumber)).thenReturn(orderDetailResponse);

        OrderDetailResponse result = mypageService.getOrderDetail(orderNumber);

        assertNotNull(result);
        assertEquals(orderNumber, result.getOrderNumber());
        assertEquals(30_000L, result.getTotalAmount());
        verify(orderAdapter).getOrder(orderNumber);
    }

    @Test
    @DisplayName("반품 신청 - 성공")
    void returnOrder_Success() {
        String orderNumber = "testOrderNumber";
        ReturnsRequest returnsRequest = new ReturnsRequest("단순 변심", false);

        doNothing().when(orderAdapter).returnOrder(orderNumber, returnsRequest);

        mypageService.returnOrder(orderNumber, returnsRequest);

        verify(orderAdapter, times(1)).returnOrder(orderNumber, returnsRequest);
    }

    @Test
    @DisplayName("반품 신청 - 실패 (OrderAdapter 예외 발생)")
    void returnOrder_Failure_OrderAdapterException() {
        String orderNumber = "testOrderNumber";
        ReturnsRequest returnsRequest = new ReturnsRequest("상품 파손", true);

        doThrow(new RuntimeException("OrderAdapter error")).when(orderAdapter).returnOrder(orderNumber, returnsRequest);

        assertThrows(RuntimeException.class, () -> mypageService.returnOrder(orderNumber, returnsRequest));

        verify(orderAdapter, times(1)).returnOrder(orderNumber, returnsRequest);
    }
}
