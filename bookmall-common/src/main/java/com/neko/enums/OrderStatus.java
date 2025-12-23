package com.neko.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED(1),     // 待付款
    PAID(2),        // 已付款
    SHIPPED(3),     // 已发货
    COMPLETED(4),   // 已完成
    CANCELLED(5);   // 已取消

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }
}
