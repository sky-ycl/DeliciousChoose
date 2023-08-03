package com.sky.service;

import com.sky.result.Result;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.io.Serializable;
import java.time.LocalDate;

public interface ReportService {


    /**
     * 统计指定时间区间内的营业额
     * @param begin
     * @param end
     * @return
     */
    Result<TurnoverReportVO> getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的用户数量
     * @param begin
     * @param end
     * @return
     */
    Result<UserReportVO> getUserStatistics(LocalDate begin, LocalDate end);


    /**
     * 统计指定时间区间内的订单数量
     * @param begin
     * @param end
     * @return
     */
    Result<OrderReportVO> getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计销量前10
     * @param begin
     * @param end
     * @return
     */
    Result<SalesTop10ReportVO> getSaleTop10(LocalDate begin, LocalDate end);
}
