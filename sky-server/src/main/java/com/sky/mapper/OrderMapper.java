package com.sky.mapper;

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
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select  * from orders where status=#{status} and order_time<#{orderTime}")
    List<Orders> getByStatusAndOrderTimeL(@Param("status") Integer status,@Param("orderTime") LocalDateTime orderTime);


    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);
}
