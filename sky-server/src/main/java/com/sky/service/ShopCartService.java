package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import org.apache.ibatis.annotations.Delete;

public interface ShopCartService {

    /**
     * 添加枸购物车
     * @param shoppingCartDTO
     * @return
     */
    Result add(ShoppingCartDTO shoppingCartDTO);


    /**
     * 获取购物车列表
     * @return
     */
    Result list();

    /**
     * 清空购物车
     * @return
     */
    Result clean();

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);


}
