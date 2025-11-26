package com.neko.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersSubmitDTO implements Serializable {
    // 地址簿id
    private Long addressBookId;
    // 付款方式
    private int payMethod;
    // 预计送达时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;
    // 运费
    private BigDecimal shippingFee;
    // 总金额
    private BigDecimal amount;
}