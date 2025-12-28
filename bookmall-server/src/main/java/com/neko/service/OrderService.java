package com.neko.service;

import com.neko.dto.OrderPageQueryDTO;
import com.neko.dto.OrderPaymentDTO;
import com.neko.dto.OrderSubmitDTO;
import com.neko.result.PageResult;
import com.neko.vo.OrderSubmitVO;
import com.neko.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO);

    /**
     * 用户端订单分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    PageResult<OrderVO> pageQuery4User(int pageNum, int pageSize, Integer status);

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 用户取消订单
     *
     * @param id
     */
    void userCancelById(Long id);

    /**
     * 条件搜索订单
     *
     * @param orderPageQueryDTO
     * @return
     */
    PageResult<OrderVO> conditionSearch(OrderPageQueryDTO orderPageQueryDTO);

    /**
     * 派送订单
     *
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);

    void reminder(Long id);

    void payment(OrderPaymentDTO orderPaymentDTO);

    void paySuccess(String orderNumber);
}
