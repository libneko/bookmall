package com.neko.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    // 名称
    private String name;

    // 订单id
    private Long orderId;

    private Long bookId;

    // 数量
    private Integer number;

    // 金额
    private BigDecimal amount;

    // 图片
    private String image;
}
