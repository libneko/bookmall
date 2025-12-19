package com.neko.enums;

import lombok.Getter;

@Getter
public enum PayStatus {
    UNPAID(0),
    PAID(1),
    REFUND(2);

    private final int code;

    PayStatus(int code) {
        this.code = code;
    }
}
