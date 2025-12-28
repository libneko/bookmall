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
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 派送订单
     *
     * @return
     */
    @PutMapping("/delivery/{id}")
    public Result<Object> delivery(@PathVariable Long id) {
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result<Object> complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }
}
