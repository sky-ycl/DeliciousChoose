package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Api(tags = "工作台相关接口")
@RestController
@RequestMapping("/admin/workspace")
public class WorkSpaceController {

    @Resource
    private WorkSpaceService workSpaceService;

    /**
     * 今日数据
     * @return
     */
    @ApiOperation("今日数据")
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData(){
        // 获取当天时间
        LocalDate now = LocalDate.now();
        // 获取当天的开始时间
        LocalDateTime beginTime = now.atTime(LocalTime.MIN);
        // 获取当天的结束时间
        LocalDateTime endTime = now.atTime(LocalTime.MAX);
        return workSpaceService.getBusinessData(beginTime,endTime);
    }


    /**
     * 查询订单管理数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> overviewOrders(){
        return workSpaceService.getOverviewOrders();
    }

    /**
     * 查询菜品总览
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> dishOverView(){
        return workSpaceService.getDishOverView();
    }

    /**
     * 查询套餐总览
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> setmealOverView(){
        return workSpaceService.getSetmealOverView();
    }
}
