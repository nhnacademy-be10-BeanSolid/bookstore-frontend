package com.nhnacademy.frontend.payment.controller;

import com.nhnacademy.frontend.payment.dto.request.PaymentApprovalRequestDto;
import com.nhnacademy.frontend.payment.dto.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.dto.response.PaymentResponseDto;
import com.nhnacademy.frontend.payment.service.PaymentService;
import com.nhnacademy.frontend.common.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @Value("${payment.toss.success-url}")
    private String successCallbackUrl;

    @Value("${payment.toss.fail-url}")
    private String failCallbackUrl;

    @GetMapping("/form")
    public String showForm(@RequestParam String orderId,
                           @RequestParam Long amount,
                           Model model) {
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount 파라미터가 필요합니다");
        }

        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setOrderId(orderId);
        dto.setPayName("도서");
        dto.setPayAmount(amount);
        dto.setSuccessUrl(successCallbackUrl);
        dto.setFailUrl(failCallbackUrl);
        dto.setUsedPoint(0);

        model.addAttribute("paymentRequest", dto);
        model.addAttribute("shippingFee",  5000);
        model.addAttribute("currentPoints", userService.getCurrentUserPoints());
        return "payments/form";
    }

    @PostMapping
    public RedirectView requestPayment(
            @Valid @ModelAttribute("paymentRequest") PaymentRequestDto dto) {

        log.debug("▶ 요청할 DTO: {}", dto);
        PaymentResponseDto resp = paymentService.requestPayment(dto);
        String redirectUrl = resp.getRedirectUrl();
        log.debug("▶ 토스로부터 받은 redirectUrl: {}", redirectUrl);

        if (redirectUrl == null || redirectUrl.isBlank()) {
            throw new IllegalStateException("결제 URL을 받지 못했습니다.");
        }
        return new RedirectView(redirectUrl, false);
    }

    @GetMapping("/success")
    public String success(@RequestParam String paymentKey,
                          @RequestParam String orderId,
                          @RequestParam Long amount,
                          Model model) {

        PaymentApprovalRequestDto dto = new PaymentApprovalRequestDto(paymentKey, orderId, amount);

        paymentService.confirmSuccess(dto);
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "payments/success";
    }

    @GetMapping("/fail")
    public String fail(@RequestParam String paymentKey,
                       @RequestParam String orderId,
                       @RequestParam(required = false) String message,
                       Model model) {

        paymentService.confirmFail(paymentKey, orderId);
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId", orderId);
        model.addAttribute("message", message);
        return "payments/fail";
    }
    @GetMapping("/payment-result")
    public String resultPage() {
        return "result";
    }
}