package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单信息
     */
    void insert(Orders orders);

    /**
     * 根据订单状态和下单时间来查询数据
     *
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select  * from orders where status=#{status} and order_time<#{orderTime}")
    List<Orders> getByStatusAndOrderTimeL(@Param("status") Integer status, @Param("orderTime") LocalDateTime orderTime);


    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    @Select("select * from orders where number=#{outTradeNo}")
    Orders getByNumber(String outTradeNo);

    /**
     * 分页条件查询并按下单时间排序
     *
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单id查询订单
     *
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);
}

