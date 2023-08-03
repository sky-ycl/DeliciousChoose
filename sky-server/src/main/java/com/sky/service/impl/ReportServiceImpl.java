package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Result<TurnoverReportVO> getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用户存放从begin到edd范围内的每一天日期
        List<LocalDate> dateList = getDateList(begin, end);
        // 存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        // 查询date日期对应的营业额数据(状态为"已完成"的订单金额合计)
        for (LocalDate date : dateList) {
            // 获取时间段的map集合
            LocalDateTime beginTime = date.atTime(LocalTime.MIN);
            LocalDateTime endTime = date.atTime(LocalTime.MAX);
            // 获取时间段的map集合
            Map<String, Object> map = getMap(beginTime, endTime, null);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.selectSumAmountOfDay(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        // 日期处理
        String dateStr = StringUtils.join(dateList, ",");
        // 每天的营业额处理
        String turnoverStr = StringUtils.join(turnoverList, ",");
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(dateStr)
                .turnoverList(turnoverStr)
                .build();
        return Result.success(turnoverReportVO);
    }


    /**
     * 统计用户时间段的数量
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Result<UserReportVO> getUserStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用户存放从begin到edd范围内的每一天日期
        List<LocalDate> dateList = getDateList(begin, end);
        // 存放新增用户数量集合
        List<Integer> newUserList = new ArrayList<>();
        // 存放总用户量集合
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {

            LocalDateTime beginTime = date.atTime(LocalTime.MIN);
            LocalDateTime endTime = date.atTime(LocalTime.MAX);

            // 获取时间段的map集合
            Map<String, Object> map = new HashMap<>();
            map.put("begin", beginTime);

            // 总用户数量
            Integer totalUserNum = userMapper.countUserNumByMap(map);
            totalUserList.add(totalUserNum);

            // 新增用户数量
            map.put("end", endTime);
            Integer newUserNum = userMapper.countUserNumByMap(map);
            newUserList.add(newUserNum);
        }

        // 日期处理
        String dateStr = StringUtils.join(dateList, ",");
        // 新增用户数量处理
        String newUserStr = StringUtils.join(newUserList, ",");
        // 总用户量处理
        String totalUserStr = StringUtils.join(totalUserList, ",");
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(dateStr)
                .newUserList(newUserStr)
                .totalUserList(totalUserStr)
                .build();
        return Result.success(userReportVO);
    }

    /**
     * 统计指定时间区间内的订单数量
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Result<OrderReportVO> getOrdersStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用户存放从begin到edd范围内的每一天日期
        List<LocalDate> dateList = getDateList(begin, end);
        // 存放每天订单总数集合
        List<Integer> orderCountList = new ArrayList<>();
        // 存放每天优先订单数量集合
        List<Integer> validOrderCountList = new ArrayList<>();
        // 计算当前时间段的订单总数量
        Integer totalOrderCount = 0;
        Integer totalValidOrderCount = 0;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = date.atTime(LocalTime.MIN);
            LocalDateTime endTime = date.atTime(LocalTime.MAX);

            // 查询当天的订单总数量
            Map<String, Object> map1 = getMap(beginTime, endTime, null);
            Integer orderCount = orderMapper.getOrderCount(map1);
            orderCountList.add(orderCount);

            // 查询当天的优先订单总量
            Map<String, Object> map2 = getMap(beginTime, endTime, Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrderCount(map2);
            validOrderCountList.add(validOrderCount);

            // 计算时间段中的总订单数量
            totalOrderCount += orderCount;
            // 计算时间段中的有效订单数量
            totalValidOrderCount += validOrderCount;

        }
        Double orderCompletionRate = 0.0;
        if (totalValidOrderCount != 0) {
            // 计算订单完成率
            orderCompletionRate = totalValidOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        // 将数据转化为字符串
        String dateListStr = StringUtils.join(dateList, ",");
        String orderCountListStr = StringUtils.join(orderCountList, ",");
        String validOrderCountListStr = StringUtils.join(validOrderCountList, ",");

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(dateListStr)
                .orderCountList(orderCountListStr)
                .validOrderCountList(validOrderCountListStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

        return Result.success(orderReportVO);
    }

    /**
     * 统计销量前10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Result<SalesTop10ReportVO> getSaleTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin.atTime(LocalTime.MIN);
        LocalDateTime endTime = end.atTime(LocalTime.MAX);
        List<GoodsSalesDTO> saleTopList=orderMapper.getSaleTop(beginTime,endTime);

        // 获取销量前10美食名字集合
        List<String> nameList = saleTopList.stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.toList());

        // 获取销量前10美食数量集合
        List<Integer> numberList = saleTopList.stream()
                .map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList());

        // 转为字符串
        String nameListStr = StringUtils.join(nameList, ",");
        String numberListStr = StringUtils.join(numberList, ",");

        // 创建salesTop10ReportVO对象
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();
        return Result.success(salesTop10ReportVO);
    }

    /**
     * 获取时间段的map集合
     *
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    private Map<String, Object> getMap(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", status);
        return map;
    }

    /**
     * 得到日期列表
     *
     * @return
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        // 当前集合用户存放从begin到edd范围内的每一天日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            // 日期计算
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
