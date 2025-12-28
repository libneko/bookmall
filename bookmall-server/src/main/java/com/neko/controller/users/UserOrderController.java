package com.neko.controller.users;

import com.neko.dto.OrderPaymentDTO;
import com.neko.dto.OrderSubmitDTO;
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
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO orderSubmitDTO) {
        log.info("用户下单，参数为：{}", orderSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    public Result<Object> payment(@RequestBody OrderPaymentDTO orderPaymentDTO) {
        log.info("订单支付：{}", orderPaymentDTO);
        orderService.payment(orderPaymentDTO);
        return Result.success();
    }

    /**
     * 历史订单查询
     *
     * @param page
     * @param pageSize
     * @param status   订单状态
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult<OrderVO>> page(int page, int pageSize, Integer status) {
        log.info("user search order(s): page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult<OrderVO> pageResult = orderService.pageQuery4User(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        OrderVO orderVO = orderService.detail(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @return
     */
    @PutMapping("/cancel/{id}")
    public Result<Object> cancel(@PathVariable Long id) {
        orderService.cancelById(id);
        return Result.success();
    }

    /**
     * 用户催单
     *
     * @param id
     * @return
     */
    @GetMapping("/reminder/{id}")
    public Result<Object> reminder(@PathVariable Long id) {
        orderService.reminder(id);
        return Result.success();
    }

    /**
     * 完成订单
     * 状态变化：DELIVERED -> COMPLETED
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result<Object> complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }
}
