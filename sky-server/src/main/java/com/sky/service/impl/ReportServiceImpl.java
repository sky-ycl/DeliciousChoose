package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    @Resource
    private WorkSpaceService workSpaceService;

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
     *
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

            // 查询当天的有效订单总量
            Map<String, Object> map2 = getMap(beginTime, endTime, Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrderCount(map2);
            validOrderCountList.add(validOrderCount);

            // 计算时间段中的总订单数量
            totalOrderCount += orderCount;
            // 计算时间段中的有效订单数量
            totalValidOrderCount += validOrderCount;

        }
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
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
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Result<SalesTop10ReportVO> getSaleTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = begin.atTime(LocalTime.MIN);
        LocalDateTime endTime = end.atTime(LocalTime.MAX);
        List<GoodsSalesDTO> saleTopList = orderMapper.getSaleTop(beginTime, endTime);

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
     * 导出运营数据报表
     *
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 查询数据库，获取营业额树---查询最近30天的数据
        LocalDate now = LocalDate.now();
        // 获取当天的开始时间
        LocalDateTime endTime = now.atTime(LocalTime.MIN);
        // 获取当天的前30天
        LocalDateTime beginTime = endTime.plusDays(-30);

        // 查询概览数据
        Result<BusinessDataVO> businessDataVO = workSpaceService.getBusinessData(beginTime, endTime);
        BusinessDataVO businessData = businessDataVO.getData();

        // 通过POI将数据写入Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        // 基于模板文件创建一个新的Excel文件
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);

            // 更换时间格式
            String beginTimeStr = beginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 获取表格的序列页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            // 填充数据--时间
            sheet.getRow(2 - 1).getCell(2 - 1).setCellValue("时间: " + beginTimeStr + " 至 " + endTimeStr);
            // 填充数据--营业额
            Double turnover = businessData.getTurnover();
            sheet.getRow(4 - 1).getCell(3 - 1).setCellValue(turnover);
            // 填充数据--订单完成率
            Double orderCompletionRate = businessData.getOrderCompletionRate();
            sheet.getRow(4 - 1).getCell(5 - 1).setCellValue(orderCompletionRate);
            // 填充数据--新增用户数量
            Integer newUsers = businessData.getNewUsers();
            sheet.getRow(4 - 1).getCell(7 - 1).setCellValue(newUsers);
            // 填充数据--有效订单
            Integer validOrderCount = businessData.getValidOrderCount();
            sheet.getRow(5 - 1).getCell(3 - 1).setCellValue(validOrderCount);
            // 填充数据--平均客单价
            Double unitPrice = businessData.getUnitPrice();
            sheet.getRow(5 - 1).getCell(5 - 1).setCellValue(unitPrice);


            // 填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDateTime date = beginTime.plusDays(i);

                // 设置时分秒
                LocalDateTime begin = date.with(LocalTime.of(0, 0, 0));
                LocalDateTime end = date.with(LocalTime.of(23, 59, 59));

                // 查询某一天的营业额数据
                businessDataVO = workSpaceService.getBusinessData(begin, end);
                businessData = businessDataVO.getData();


                // 获取某一行
                XSSFRow row = sheet.getRow((8 - 1) + i);
                // 填充数据
                row.getCell(2 - 1).setCellValue(begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                row.getCell(3 - 1).setCellValue(businessData.getTurnover());
                row.getCell(4 - 1).setCellValue(businessData.getValidOrderCount());
                row.getCell(5 - 1).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(6 - 1).setCellValue(businessData.getUnitPrice());
                row.getCell(7 - 1).setCellValue(businessData.getNewUsers());
            }

            // 通过输出流将Excel文件下载到客户端你浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            // 关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
