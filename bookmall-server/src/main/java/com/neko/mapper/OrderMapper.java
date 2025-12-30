package com.neko.mapper;

import com.neko.dto.OrderPageQueryDTO;
import com.neko.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Order order);

    /**
     * 根据订单号和用户 id 查询订单
     *
     * @param orderNumber
     * @param userId
     */
    @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
    Order getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     *
     * @param order
     */
    void update(Order order);

    /**
     * 分页条件查询并按下单时间排序
     *
     * @param orderPageQueryDTO
     */
    List<Order> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    /**
     * 条件查询订单数量
     *
     * @param orderPageQueryDTO
     */
    Long count(OrderPageQueryDTO orderPageQueryDTO);

    /**
     * 根据 id 查询订单
     *
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Order getById(Long id);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Order> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据状态统计订单数量
     *
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    Double sumByMap(Map<String, Object> map);

    Integer countByMap(Map<String, Object> map);

    @Select("select * from orders where number = #{orderNumber}")
    Order getByNumber(String orderNumber);
}
