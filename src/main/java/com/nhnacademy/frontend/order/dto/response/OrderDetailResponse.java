package com.nhnacademy.frontend.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderDetailResponse {

    private LocalDate orderDate;
    private String orderId;
    private String status;
    private Long totalAmount;
    private List<ItemInfo> itemInfos;
    private String receiverName;
    private String receiverPhoneNumber;
    private String address;
    private LocalDate requestedDeliveryDate;
    private Integer deliveryFee;

    @Getter
    @AllArgsConstructor
    public static class ItemInfo {

        private String title;
        private Integer quantity;
        private Long bookPrice;
        private String wrappingName;
        private Integer wrappingPrice;
    }
}
