package com.neko.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING_PAYMENT(1),
    TO_BE_CONFIRMED(2),
    CONFIRMED(3),
    DELIVERY_IN_PROGRESS(4),
    COMPLETED(5),
    CANCELLED(6);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }
}
