package com.neko.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.neko.constant.MessageConstant;
import com.neko.context.BaseContext;
import com.neko.dto.OrdersPageQueryDTO;
import com.neko.dto.OrdersSubmitDTO;
import com.neko.entity.AddressBook;
import com.neko.entity.OrderDetail;
import com.neko.entity.Order;
import com.neko.entity.ShoppingCart;
import com.neko.enums.OrderStatus;
import com.neko.enums.PayStatus;
import com.neko.exception.AddressBookBusinessException;
import com.neko.exception.OrderBusinessException;
import com.neko.exception.ShoppingCartBusinessException;
import com.neko.mapper.AddressBookMapper;
import com.neko.mapper.OrderDetailMapper;
import com.neko.mapper.OrderMapper;
import com.neko.mapper.ShoppingCartMapper;
import com.neko.result.PageResult;
import com.neko.service.OrderService;
import com.neko.vo.OrderSubmitVO;
import com.neko.vo.OrderVO;
import com.neko.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final WebSocketServer webSocketServer;
    private final ObjectMapper objectMapper;

    public OrderServiceImpl(AddressBookMapper addressBookMapper, ShoppingCartMapper shoppingCartMapper, OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, WebSocketServer webSocketServer, ObjectMapper objectMapper) {
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.webSocketServer = webSocketServer;
        this.objectMapper = objectMapper;
    }

    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 处理异常情况
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        // 查询购物车数据
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 插入数据
        Order order = new Order();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(PayStatus.UNPAID.getCode());
        order.setStatus(OrderStatus.CREATED.getCode());
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setConsignee(addressBook.getConsignee());
        order.setUserId(userId);

        orderMapper.insert(order);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailList);

        shoppingCartMapper.deleteByUserId(userId);

        return OrderSubmitVO.builder()
                .id(order.getId())
                .orderTime(order.getOrderTime())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .build();
    }

    @Override
    public PageResult<OrderVO> pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Order> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Order order : page) {
                Long orderId = order.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(order, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public OrderVO details(Long id) {
        // 根据 id 查询订单
        Order order = orderMapper.getById(id);

        // 查询该订单对应的书本明细
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    @Override
    public void userCancelById(Long id) {
        // 根据 id 查询订单
        Order orderDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (orderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        if (orderDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Order order = new Order();
        order.setId(orderDB.getId());

        // 订单处于已付款下取消，需要进行退款
        if (orderDB.getStatus().equals(OrderStatus.PAID.getCode())) {
            //支付状态修改为 退款
            order.setPayStatus(PayStatus.REFUND.getCode());
        }

        // 更新订单状态、取消原因、取消时间
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }

    public PageResult<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

        Page<Order> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult<>(page.getTotal(), orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<Order> page) {
        return page.getResult().stream()
                .map(order -> {
                    OrderVO vo = new OrderVO();
                    BeanUtils.copyProperties(order, vo);
                    return vo;
                })
                .toList();
    }

    @Override
    public void delivery(Long id) {
        // 根据id查询订单
        Order orderDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为 PAID
        if (orderDB == null || !orderDB.getStatus().equals(OrderStatus.PAID.getCode())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Order order = new Order();
        order.setId(orderDB.getId());
        // 更新订单状态,状态转为已发货
        order.setStatus(OrderStatus.SHIPPED.getCode());

        orderMapper.update(order);
    }

    @Override
    public void complete(Long id) {
        // 根据id查询订单
        Order orderDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为 SHIPPED
        if (orderDB == null || !orderDB.getStatus().equals(OrderStatus.SHIPPED.getCode())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Order order = new Order();
        order.setId(orderDB.getId());
        // 更新订单状态,状态转为完成
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(order);
    }

    @Override
    public void reminder(Long id) {
        Order order = orderMapper.getById(id);

        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map<String, Object> map = Map.of(
                "type", 2,
                "orderId", id,
                "content", "Order number: " + order.getNumber()
        );

        try {
            webSocketServer.sendToAllClient(objectMapper.writeValueAsString(map));
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
