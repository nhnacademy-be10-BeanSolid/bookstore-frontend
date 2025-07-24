package com.nhnacademy.frontend.mypage.controller;


import com.nhnacademy.frontend.auth.principal.CustomPrincipal;
import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookLikeResponse;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePoint;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import com.nhnacademy.frontend.common.advice.GlobalModelAttributeAdvice;
import com.nhnacademy.frontend.mypage.service.MypageService;
import com.nhnacademy.frontend.order.dto.response.OrderDetailResponse;
import com.nhnacademy.frontend.order.dto.response.OrderSummaryResponse;
import feign.FeignException;
import feign.Request;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static feign.Response.builder;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MyPageController 단위 테스트")
class MyPageControllerTest {

    @Mock
    private MypageService mypageService;

    @Mock
    private JwtCookieUtil jwtCookieUtil;

    @InjectMocks
    private MypageController mypageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders.standaloneSetup(mypageController)
                .setCustomArgumentResolvers(pageableResolver)
                .setControllerAdvice(new GlobalModelAttributeAdvice())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("마이페이지 조회")
    void mypageForm() throws Exception {
        mockMvc.perform(get("/mypage"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/form"));
    }

    @Test
    @DisplayName("마이페이지 회원 탈퇴 - LOCAL 사용자 성공")
    void mypageWithdraw_LocalUser_Success() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "LOCAL");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.when(mypageService.withdrawUser("testPassword")).thenReturn(true);

        mockMvc.perform(post("/mypage/withdraw")
                        .param("password", "testPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Mockito.verify(mypageService).withdrawUser("testPassword");
        Mockito.verify(jwtCookieUtil).removeJwtCookie(any(HttpServletResponse.class));
    }


    @Test
    @DisplayName("마이페이지 회원 탈퇴 - LOCAL 유저 비밀번호 누락")
    void mypageWithdraw_LocalUser_MissingPassword() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "LOCAL");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(post("/mypage/withdraw")
                        .requestAttr("userType", "LOCAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage?error=password_required"));

        Mockito.verify(mypageService, Mockito.never()).withdrawUser(any());
        Mockito.verify(jwtCookieUtil, Mockito.never()).removeJwtCookie(any());
    }

    @Test
    @DisplayName("마이페이지 회원 탈퇴 - OAUTH2 유저 성공")
    void mypageWithdraw_OAuth2User_Success() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "OAUTH2");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
        Mockito.when(mypageService.withdrawOAuth2User()).thenReturn(true);

        mockMvc.perform(post("/mypage/withdraw")
                        .requestAttr("userType", "OAUTH2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Mockito.verify(mypageService).withdrawOAuth2User();
        Mockito.verify(jwtCookieUtil).removeJwtCookie(any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("마이페이지 회원 탈퇴 - 잘못된 userType")
    void mypageWithdraw_InvalidUserType() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "INVALID");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
        mockMvc.perform(post("/mypage/withdraw")
                        .requestAttr("userType", "INVALID"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage?error=invalid_user_type"));

        Mockito.verify(mypageService, Mockito.never()).withdrawUser(any());
        Mockito.verify(mypageService, Mockito.never()).withdrawOAuth2User();
        Mockito.verify(jwtCookieUtil, Mockito.never()).removeJwtCookie(any());
    }

    @Test
    @DisplayName("마이페이지 회원 탈퇴 - LOCAL 유저 실패 시 redirect")
    void mypageWithdraw_LocalUser_Failure() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "LOCAL");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
        Mockito.when(mypageService.withdrawUser("wrongPassword")).thenReturn(false);

        mockMvc.perform(post("/mypage/withdraw")
                        .param("password", "wrongPassword")
                        .requestAttr("userType", "LOCAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage"));

        Mockito.verify(mypageService).withdrawUser("wrongPassword");
        Mockito.verify(jwtCookieUtil, Mockito.never()).removeJwtCookie(any());
    }

    @Test
    @DisplayName("마이페이지 회원 정보 수정 폼 조회")
    void mypageEditForm() throws Exception {
        mockMvc.perform(get("/mypage/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/edit"));

        Mockito.verify(mypageService).getMyInfo();
    }

    @Test
    @DisplayName("마이페이지 회원 정보 수정 - 비밀번호 일치")
    void editMyPage_PasswordMatch() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "LOCAL");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);

        String password = "correctPassword";

        Mockito.when(mypageService.updatePersonalInformationWithPassword(password)).thenReturn(true);

        mockMvc.perform(post("/mypage/edit")
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage"));

        Mockito.verify(mypageService).updatePersonalInformationWithPassword(password);
    }

    @Test
    @DisplayName("마이페이지 회원 정보 수정 - 비밀번호 불일치")
    void editMyPage_PasswordMismatch() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "LOCAL");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
        String password = "wrongPassword";


        Mockito.when(mypageService.updatePersonalInformationWithPassword(password)).thenReturn(false);

        mockMvc.perform(post("/mypage/edit")
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/myinfo"));

        Mockito.verify(mypageService).updatePersonalInformationWithPassword(password);

    }

    @Test
    @DisplayName("마이페이지 회원 정보 수정 - 비밀번호와 비밀번호 확인 불일치")
    void editMyPage_PasswordAndConfirmMismatch() throws Exception {
        String password = "newPassword";
        String userPassword = "newPassword";
        String userPasswordConfirm = "differentPassword";

        mockMvc.perform(post("/mypage/edit")
                        .param("password", password)
                        .param("userPassword", userPassword)
                        .param("userPasswordConfirm", userPasswordConfirm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/edit"));

        Mockito.verify(mypageService, Mockito.never()).updatePersonalInformationWithPassword(any());
    }

    @Test
    @DisplayName("마이페이지 회원 정보 수정 - 비밀번호와 비밀번호 확인 일치")
    void editMyPage_PasswordAndConfirmMatch() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "LOCAL");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
        String password = "newPassword";
        String userPassword = "newPassword";
        String userPasswordConfirm = "newPassword";

        Mockito.when(mypageService.updatePersonalInformationWithPassword(password)).thenReturn(true);

        mockMvc.perform(post("/mypage/edit")
                        .param("password", password)
                        .param("userPassword", userPassword)
                        .param("userPasswordConfirm", userPasswordConfirm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage"));

        Mockito.verify(mypageService).updatePersonalInformationWithPassword(password);
    }

    @Test
    @DisplayName("마이페이지 회원 주소 조회")
    void mypageAddressForm() throws Exception {
        mockMvc.perform(get("/mypage/address"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/address"));

        Mockito.verify(mypageService).getAllAddresses();
    }

    @Test
    @DisplayName("마이페이지 회원 주소 삭제")
    void deleteAddress() throws Exception {
        Long addressId = 1L;

        mockMvc.perform(delete("/mypage/address/{addressId}", addressId)
                        .param("addressId", String.valueOf(addressId)))
                .andExpect(status().isNoContent());

        Mockito.verify(mypageService).deleteAddress(addressId);
    }

    @Test
    @DisplayName("마이페이지 회원 주소 등록 - 성공")
    void addAddress_Success() throws Exception {
        String addressNickName = "주소 별칭";
        String addressDetail = "상세 주소";
        mockMvc.perform(post("/mypage/address/register")
                        .param("addressNickName", addressNickName)
                        .param("addressDetail", addressDetail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/address"));
        Mockito.verify(mypageService).addAddress(Mockito.any());
    }

    @Test
    @DisplayName("마이페이지 회원 주소 등록 - 실패 (주소 10개 초과)")
    void addAddress_Failure_TooManyAddresses() throws Exception {
        String addressNickName = "주소 별칭";
        String addressDetail = "상세 주소";

        Mockito.doThrow(
                FeignException.errorStatus("addAddress",
                        builder()
                                .status(400)
                                .reason("Bad Request")
                                .request(Request.create(
                                        Request.HttpMethod.POST,
                                        "http://test",
                                        Map.of(),
                                        null,
                                        UTF_8,
                                        null))
                                .build()))
                .when(mypageService).addAddress(Mockito.any());

        mockMvc.perform(post("/mypage/address/register")
                        .param("addressNickName", addressNickName)
                        .param("addressDetail", addressDetail))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/address"))
                .andExpect(flash().attributeExists("errorMessage"));

        Mockito.verify(mypageService).addAddress(any());
    }

    @Test
    @DisplayName("마이페이지 회원 주소 등록 폼")
    void mypageAddressRegisterForm() throws Exception {
        mockMvc.perform(get("/mypage/address/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("address-register"));
    }

    @Test
    @DisplayName("마이페이지 포인트 조회")
    void mypagePointForm() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<ResponsePoint> pointsList = List.of();

        Mockito.when(mypageService.getAllPoints(pageable))
                .thenReturn(new PageImpl<>(pointsList, pageable, 0));
        Mockito.when(mypageService.getUserPoint()).thenReturn(1000);

        mockMvc.perform(get("/mypage/point")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/point"));

        Mockito.verify(mypageService).getAllPoints(pageable);
        Mockito.verify(mypageService).getUserPoint();
    }

    @Test
    @DisplayName("마이페이지 회원 정보 조회 전 비밀번호 입력페이지 - LOCAL 사용자")
    void mypageInfo() throws Exception {
        CustomPrincipal principal = new CustomPrincipal("testUserId", "LOCAL");

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/mypage/myinfo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mypage/verify"));

    }

    @Test
    @DisplayName("마이페이지 회원 정보 조회(비밀번호 입력 후) - LOCAL 사용자")
    void mypageInfoAfterPasswordInput() throws Exception {

        Mockito.when(mypageService.getMyInfo()).thenReturn(new ResponseUser(
                1L, "Test User", "asdfghjkl", "test", "010-1111-1111", "asdf@asdf.asdf", LocalDate.now(), 1000, false, null, LocalDateTime.now(), null, null));
        mockMvc.perform(get("/mypage/myinfo")
                        .sessionAttr("mypage_verified", true))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/myinfo"))
                .andExpect(model().attributeExists("user"));
        Mockito.verify(mypageService).getMyInfo();
    }

    @Test
    @DisplayName("비밀번호 수정 폼 조회")
    void mypageEditPasswordForm() throws Exception {
        mockMvc.perform(get("/mypage/editpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/editpassword"));
        Mockito.verify(mypageService).getMyInfo();
    }

    @Test
    @DisplayName("마이페이지 비밀번호 인증 폼 조회")
    void mypageVerifyForm() throws Exception {
        mockMvc.perform(get("/mypage/verify"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/verify"));
    }

    @Test
    @DisplayName("마이페이지 비밀번호 인증 성공")
    void verifyPassword_Success() throws Exception {
        String password = "correctPassword";

        Mockito.when(mypageService.updatePersonalInformationWithPassword(password)).thenReturn(true);

        mockMvc.perform(post("/mypage/verify")
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mypage/myinfo"));

        Mockito.verify(mypageService).updatePersonalInformationWithPassword(password);
    }

    @Test
    @DisplayName("마이페이지 비밀번호 인증 실패")
    void verifyPassword_Failure() throws Exception {
        String password = "wrongPassword";

        Mockito.when(mypageService.updatePersonalInformationWithPassword(password)).thenReturn(false);

        mockMvc.perform(post("/mypage/verify")
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mypage/verify"))
                .andExpect(flash().attributeExists("error"));

        Mockito.verify(mypageService).updatePersonalInformationWithPassword(password);
    }

    @Test
    @DisplayName("마이페이지 등급 조회")
    void mypageGradeForm() throws Exception {
        Mockito.when(mypageService.getMyInfo()).thenReturn(new ResponseUser(
                1L, "Test User", "asdfghjkl", "test", "010-1111-1111", "test@test.test", LocalDate.now(), 1000, false, null, LocalDateTime.now(),null, null));
        mockMvc.perform(get("/mypage/grade"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/grade"));
    }

    @Test
    @DisplayName("마이페이지 등급 업데이트")
    void updateUserGrade() throws Exception {
        mockMvc.perform(post("/mypage/grade/update"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mypage/grade"));

        Mockito.verify(mypageService).bulkUpdateUserGrades();
    }

    @Test
    @DisplayName("마이페이지 주문 목록 조회에 성공하면 200을 응답한다")
    void mypageOrdersForm_Success() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        OrderSummaryResponse summary = new OrderSummaryResponse(LocalDate.now(), "202507-abcdef-123456", "받는 사람", 30000L, "PENDING");
        Page<OrderSummaryResponse> orderPage = new PageImpl<>(List.of(summary), pageable, 1);

        Mockito.when(mypageService.getAllOrders(any(Pageable.class))).thenReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/mypage/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/order-list"))
                .andExpect(model().attribute("orders", orderPage));

        Mockito.verify(mypageService).getAllOrders(any(Pageable.class));
    }

    @Test
    @DisplayName("마이페이지 주문 상세 조회에 성공하면 200을 응답한다")
    void getOrderDetail_Success() throws Exception {
        // Given
        String orderNumber = "202507-abcdef-123456";
        OrderDetailResponse orderDetail = OrderDetailResponse.builder()
                .orderNumber(orderNumber)
                .totalAmount(30_000L)
                .build();

        Mockito.when(mypageService.getOrderDetail(orderNumber)).thenReturn(orderDetail);

        // When & Then
        mockMvc.perform(get("/mypage/orders/{orderNumber}", orderNumber))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/order-detail"))
                .andExpect(model().attribute("order", orderDetail));

        Mockito.verify(mypageService).getOrderDetail(orderNumber);
    }

    @Test
    @DisplayName("반품 신청 - 성공")
    void returnOrder_Success() throws Exception {
        String orderNumber = "testOrderNumber";
        String reason = "단순 변심";
        boolean damaged = false;

        mockMvc.perform(post("/mypage/orders/{orderNumber}/return", orderNumber)
                        .param("reason", reason)
                        .param("damaged", String.valueOf(damaged)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/orders"))
                .andExpect(flash().attributeExists("message"));

        Mockito.verify(mypageService).returnOrder(orderNumber, reason, damaged);
    }

    @Test
    @DisplayName("반품 신청 - 실패")
    void returnOrder_Failure() throws Exception {
        String orderNumber = "testOrderNumber";
        String reason = "상품 파손";
        boolean damaged = true;

        Mockito.doThrow(new RuntimeException("Service error"))
                .when(mypageService).returnOrder(orderNumber, reason, damaged);

        mockMvc.perform(post("/mypage/orders/{orderNumber}/return", orderNumber)
                        .param("reason", reason)
                        .param("damaged", String.valueOf(damaged)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/orders"))
                .andExpect(flash().attributeExists("errorMessage"));

        Mockito.verify(mypageService).returnOrder(orderNumber, reason, damaged);
    }

    @Test
    @DisplayName("좋아요한 책 목록 조회")
    void getBookLikes() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookLikeResponse> bookLikes = List.of(
                new BookLikeResponse(1L, LocalDateTime.now(), "test", 1L, "testBook"),
                new BookLikeResponse(2L, LocalDateTime.now(), "test", 2L, "testBook2")
        );
        Page<BookLikeResponse> bookLikePage = new PageImpl<>(bookLikes, pageable, bookLikes.size());

        Mockito.when(mypageService.getBookLikes(pageable)).thenReturn(bookLikePage);

        mockMvc.perform(get("/mypage/book-likes")
                        .header("X-USER-ID", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("mypage/book-likes"))
                .andExpect(model().attribute("bookLikes", bookLikePage));

        Mockito.verify(mypageService).getBookLikes(pageable);
    }
}
