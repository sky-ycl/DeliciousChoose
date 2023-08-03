package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据
     *
     * @return
     */
    @Override
    public Result<BusinessDataVO> getBusinessData(LocalDateTime beginTime, LocalDateTime endTime) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */

        // 创建map集合 存放时间数据
        Map<String, Object> map = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endTime);

        // 查询当天的新增用户
        Integer newUsers = userMapper.countUserNumByMap(map);
        // 获取总订单数量
        Integer totalOrderCount = orderMapper.getOrderCount(map);
        map.put("status", 5);


        // 获取当天的营业额
        Double turnover = orderMapper.selectSumAmountOfDay(map);
        turnover = turnover == null ? 0.0 : turnover;
        // 获取有效订单数量
        Integer totalValidOrderCount = orderMapper.getOrderCount(map);

        // 平均客单价
        Double unitPrice = 0.0;
        // 订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0 && totalValidOrderCount != 0) {
            // 计算客单价格
            unitPrice = turnover.doubleValue() / totalValidOrderCount.doubleValue();
            // 计算订单完成率
            orderCompletionRate = totalValidOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        BusinessDataVO businessDataVO = BusinessDataVO.builder()
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .validOrderCount(totalValidOrderCount)
                .build();

        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @Override
    public Result<OrderOverViewVO> getOverviewOrders() {

        Map map = new HashMap();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", Orders.TO_BE_CONFIRMED);
        //待接单
        Integer waitingOrders = orderMapper.getOrderCount(map);

        //待派送
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.getOrderCount(map);

        //已完成
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.getOrderCount(map);

        //已取消
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.getOrderCount(map);

        //全部订单
        map.put("status", null);
        Integer allOrders = orderMapper.getOrderCount(map);

        OrderOverViewVO orderOverViewVO = OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
        return Result.success(orderOverViewVO);
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @Override
    public Result<DishOverViewVO> getDishOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(map);

        DishOverViewVO dishOverViewVO = DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
        return Result.success(dishOverViewVO);
    }


    /**
     * 查询套餐总览
     *
     * @return
     */
    @Override
    public Result<SetmealOverViewVO> getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(map);

        SetmealOverViewVO setmealOverViewVO = SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
        return Result.success(setmealOverViewVO);
    }
}
