package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.MyThreadLocal;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private AddressBookMapper addressBookMapper;

    @Resource
    private ShopCartMapper shopCartMapper;

    @Override
    public Result submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        // 处理各种业务异常
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        // 抛出业务异常
        if(addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        // 查询当前业务车的购物车数据
        List<ShoppingCart> shoppingCartList = shopCartMapper.list(ShoppingCart.builder()
                .userId(MyThreadLocal.getCurrentId())
                .build());
        if(shoppingCartList==null || shoppingCartList.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 向订单表插入数据
        Orders orders = new Orders();
        // 赋值属性
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(LocalDateTime.now()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(MyThreadLocal.getCurrentId());
        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList=new ArrayList<>();
        // 向订单明细表插入n条数据
        shoppingCartList.forEach(shoppingCart -> {
            // 订单明细
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        });

        // 批量插入
        orderDetailMapper.insertBatch(orderDetailList);

        // 清空购物车
        shopCartMapper.deleteById(MyThreadLocal.getCurrentId());

        // 封装VO返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();
        return Result.success(orderSubmitVO);

    }
}
