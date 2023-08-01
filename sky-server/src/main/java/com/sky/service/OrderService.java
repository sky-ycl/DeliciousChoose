package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    Result submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
