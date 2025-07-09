package com.nhnacademy.frontend.mypage.controller;


import com.nhnacademy.frontend.auth.util.JwtCookieUtil;
import com.nhnacademy.frontend.admin.domain.response.ResponsePoint;
import com.nhnacademy.frontend.common.adapter.domain.response.ResponseUser;
import com.nhnacademy.frontend.mypage.domain.request.AddressCreateRequest;
import com.nhnacademy.frontend.mypage.domain.request.UserUpdateRequestDto;
import com.nhnacademy.frontend.mypage.service.MypageService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;
    private final JwtCookieUtil jwtCookieUtil;


    @GetMapping
    public String mypageForm() {
        return "mypage/form";
    }

    @PostMapping("/withdraw")
    public String mypageWithdraw(@RequestParam(value = "password", required = false) String password,
                                 HttpServletResponse response,
                                 Model model) {
        String userType = (String) model.getAttribute("userType");

        boolean result;

        if("OAUTH2".equals(userType)) {
            result = mypageService.withdrawOAuth2User();
        } else if ("LOCAL".equals(userType)) {
            if(password == null || password.isEmpty()) {
                return "redirect:/mypage?error=password_required";
            }
            result = mypageService.withdrawUser(password);
        } else {
            return "redirect:/mypage?error=invalid_user_type";
        }

        if(result) {
            jwtCookieUtil.removeJwtCookie(response);
            return "redirect:/";
        } else {
            return "redirect:/mypage";
        }
    }

    @GetMapping("/edit")
    public String mypageEditForm(Model model) {
        ResponseUser user = mypageService.getMyInfo();
        model.addAttribute("user", user);
        return "mypage/edit";
    }

    @PutMapping("/edit")
    @ResponseBody
    public ResponseEntity<Void> mypageEdit(@RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        mypageService.updatePersonalInformation(userUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/address")
    public String mypageAddressForm(Model model) {
        model.addAttribute("addresses", mypageService.getAllAddresses());
        return "mypage/address";
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Void> mypageDeleteAddress(@PathVariable Long addressId) {
        mypageService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/address/register")
    public String mypageAddAddress(@ModelAttribute AddressCreateRequest addressCreateRequest, RedirectAttributes redirectAttributes) {
        try {
            mypageService.addAddress(addressCreateRequest);
        } catch (FeignException.BadRequest e) {
            // 10개 초과로 등록 시
            redirectAttributes.addFlashAttribute("errorMessage", "주소는 10개까지 등록 가능합니다.");
            return "redirect:/mypage/address"; // 주소 목록 페이지로 리다이렉트
        }
        return "redirect:/mypage/address";
    }

    @GetMapping("/address/register")
    public String mypageAddressRegisterForm() {
        return "mypage/address_register";
    }

    @GetMapping("/point")
    public String mypagePointForm(Pageable pageable, Model model) {

        Page<ResponsePoint> points = mypageService.getAllPoints(pageable);


        int newPoint = mypageService.getUserPoint();

        model.addAttribute("newPoint", newPoint);
        model.addAttribute("points", points.getContent());
        model.addAttribute("page", points);

        return "mypage/point";
    }
}
