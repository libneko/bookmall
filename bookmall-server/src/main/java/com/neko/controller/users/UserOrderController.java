package com.neko.controller.users;

import com.neko.dto.OrdersSubmitDTO;
import com.neko.result.PageResult;
import com.neko.result.Result;
import com.neko.service.OrderService;
import com.neko.vo.OrderSubmitVO;
import com.neko.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Slf4j
public class UserOrderController {

    private final OrderService orderService;

    public UserOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单，参数为：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 历史订单查询
     *
     * @param page
     * @param pageSize
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult<OrderVO>> page(int page, int pageSize, Integer status) {
        PageResult<OrderVO> pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @return
     */
    @PutMapping("/cancel/{id}")
    public Result<Object> cancel(@PathVariable("id") Long id) {
        orderService.userCancelById(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    public Result<Object> reminder(@PathVariable Long id) {
        orderService.reminder(id);
        return Result.success();
    }
}
