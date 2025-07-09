package com.nhnacademy.frontend.payment.controller;

import com.nhnacademy.frontend.payment.domain.request.PaymentRequestDto;
import com.nhnacademy.frontend.payment.domain.response.PaymentResponseDto;
import com.nhnacademy.frontend.payment.service.PaymentService;
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

    // ← 여기에 반드시 선언되어 있어야 합니다.
    @Value("${frontend.base-url}")
    private String frontendBase;

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String orderId,
                           @RequestParam(required = false) Long amount,
                           Model model) {
        return buildForm(orderId, amount, model);
    }

    @PostMapping
    public RedirectView requestPayment(@Valid @ModelAttribute("paymentRequest") PaymentRequestDto dto) {
        log.debug("POST /payments dto={}", dto);
        PaymentResponseDto resp = paymentService.requestPayment(dto);
        String url = resp.getRedirectUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("결제 URL을 받지 못했습니다. 서버 로그 확인");
        }
        return new RedirectView(url, false);
    }

    @GetMapping("/success")
    public String success(@RequestParam String paymentKey,
                          @RequestParam String orderId,
                          @RequestParam Long amount,
                          Model model) {
        paymentService.confirmSuccess(paymentKey, orderId, amount);
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId",    orderId);
        model.addAttribute("amount",     amount);
        return "payments/success";
    }

    @GetMapping("/fail")
    public String fail(@RequestParam String paymentKey,
                       @RequestParam String orderId,
                       Model model) {
        paymentService.confirmFail(paymentKey, orderId);
        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderId",    orderId);
        return "payments/fail";
    }

    private String buildForm(String orderId,
                             Long amount,
                             Model model) {

        if (amount == null || amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount 파라미터가 필요합니다");
        }

        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setOrderId(orderId);
        dto.setPayName("도서");
        dto.setPayAmount(amount);
        // 앞에서 @Value로 주입받은 운영 도메인(예: https://bookstore-beansolid.store) 사용
        dto.setSuccessUrl(frontendBase + "/payments/success");
        dto.setFailUrl(frontendBase    + "/payments/fail");

        model.addAttribute("paymentRequest", dto);
        return "payments/form";
    }
}