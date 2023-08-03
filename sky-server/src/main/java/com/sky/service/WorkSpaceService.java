package com.sky.service;

import com.sky.result.Result;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {

    /**
     * 今日数据
     * @return
     */
    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    Result<BusinessDataVO> getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     * @return
     */
    Result<OrderOverViewVO> getOverviewOrders();


    /**
     * 查询菜品总览
     * @return
     */
    Result<DishOverViewVO> getDishOverView();

    /**
     * 查询套餐总览
     * @return
     */
    Result<SetmealOverViewVO> getSetmealOverView();
}
