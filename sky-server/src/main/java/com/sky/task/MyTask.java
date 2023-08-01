package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class MyTask {

    @Resource
    private OrderMapper orderMapper;

    /**
     * 处理外卖超时的订单
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void executeTask() {
        Date date = new Date();
        log.info("定时开启:" + date);
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time = now.plusMinutes(-15);
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeL(Orders.PENDING_PAYMENT, time);
        if (orderList != null && orderList.size() > 0) {
            for (Orders orders : orderList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时,自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直在派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")// 每天凌晨一点执行一次
    public void processDeliverOrder(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time = now.plusHours(-1);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeL(Orders.DELIVERY_IN_PROGRESS, time);
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
