package com.neko.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    // 订单号
    private String number;

    // 订单状态
    private Integer status;

    // 下单用户 id
    private Long userId;

    // 地址 id
    private Long addressBookId;

    // 下单时间
    private LocalDateTime orderTime;

    // 结账时间
    private LocalDateTime checkoutTime;

    // 支付方式 1微信，2支付宝
    private Integer payMethod;

    // 支付状态 0未支付 1已支付 2退款
    private Integer payStatus;

    // 实收金额
    private BigDecimal amount;

    // 用户名
    private String userName;

    // 手机号
    private String phone;

    // 收货人
    private String consignee;

    // 订单取消时间
    private LocalDateTime cancelTime;

    // 预计送达时间
    private LocalDateTime estimatedDeliveryTime;

    // 送达时间
    private LocalDateTime deliveryTime;

    // 运费
    private BigDecimal shippingFee;
}