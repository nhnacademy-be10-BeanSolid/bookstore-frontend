package com.nhnacademy.frontend.payment.controller;

import com.nhnacademy.frontend.payment.domain.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.domain.response.PaymentResponseDto;
import com.nhnacademy.frontend.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/form")
    public String showFormQuery(@RequestParam(required = false) String orderId,
                                @RequestParam(required = false) Long amount,
                                HttpServletRequest req,
                                Model model) {
        return buildForm(orderId, amount, req, model);
    }

    @GetMapping("/form/{orderId}")
    public String showFormPath(@PathVariable String orderId,
                               @RequestParam(required = false) Long amount,
                               HttpServletRequest req,
                               Model model) {
        return buildForm(orderId, amount, req, model);
    }

    @PostMapping
    public RedirectView requestPayment(@Valid @ModelAttribute("paymentRequest") PaymentRequestDto dto) {
        PaymentResponseDto resp = paymentService.requestPayment(dto);
        String url = resp.getRedirectUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("결제 URL을 받지 못했습니다. 서버 로그 확인");
        }
        return new RedirectView(url, false);
    }

    @GetMapping("/success")
    public String success(@RequestParam String paymentKey,
                          @RequestParam String orderId) {
        paymentService.confirmSuccess(paymentKey, orderId);
        return "payments/success";
    }

    @GetMapping("/fail")
    public String fail(@RequestParam String paymentKey,
                       @RequestParam String orderId) {
        paymentService.confirmFail(paymentKey, orderId);
        return "payments/fail";
    }

    private String buildForm(String orderId,
                             Long amount,
                             HttpServletRequest req,
                             Model model) {

        if (amount == null || amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount 파라미터가 필요합니다");
        }

        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setOrderId(orderId);
        dto.setPayName("도서");
        dto.setPayAmount(amount);

        // ★ 콜백 URL을 백엔드 고정 HTTPS 주소로
        dto.setSuccessUrl("https://bookstore-beansolid.store/api/v1/payments/success");
        dto.setFailUrl   ("https://bookstore-beansolid.store/api/v1/payments/fail");

        model.addAttribute("paymentRequest", dto);
        return "payments/form";
    }
}