package com.neko.controller.admin;

import com.neko.dto.OrderPageQueryDTO;
import com.neko.result.PageResult;
import com.neko.result.Result;
import com.neko.service.OrderService;
import com.neko.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/conditionSearch")
    public Result<PageResult<OrderVO>> conditionSearch(OrderPageQueryDTO orderPageQueryDTO) {
        log.info("Admin search order(s): {}", orderPageQueryDTO);
        PageResult<OrderVO> pageResult = orderService.conditionSearch(orderPageQueryDTO);
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
     * 派送订单
     * 状态变化：PAID -> SHIPPED
     *
     * @return
     */
    @PutMapping("/ship/{id}")
    public Result<Object> ship(@PathVariable Long id) {
        orderService.ship(id);
        return Result.success();
    }

    /**
     * 送达订单
     * 状态变化：SHIPPED -> DELIVERED
     *
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result<Object> delivery(@PathVariable Long id) {
        orderService.delivery(id);
        return Result.success();
    }
}
