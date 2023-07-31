package com.sky.service.impl;

import com.sky.context.MyThreadLocal;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShopCartMapper;
import com.sky.result.Result;
import com.sky.service.ShopCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopCartServiceImpl implements ShopCartService {

    @Resource
    private ShopCartMapper shopCartMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @Override
    public Result add(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前加入购物车的菜品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(MyThreadLocal.getCurrentId());
        List<ShoppingCart> list = shopCartMapper.list(shoppingCart);

        // 如果存在就修改数量
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shopCartMapper.updateNumberById(cart);
            return Result.success();
        }
        // 如果不存在就插入一条数据
        // 判断添加时菜品还是套餐
        Long dishId = shoppingCartDTO.getDishId();
        if (dishId != null) {
            // 添加到购物车的是菜品
            Dish dish = dishMapper.selectDish(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        }else{
            // 添加到购物车的是套餐
            Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shopCartMapper.insert(shoppingCart);
        return Result.success();
    }

    /**
     * 获取购物车列表
     * @return
     */
    @Override
    public Result list() {
        List<ShoppingCart> shoppingCartList = shopCartMapper.list(null);
        return Result.success(shoppingCartList);
    }

    /**
     * 清空购物车
     * @return
     */
    @Override
    public Result clean() {
        shopCartMapper.clear();
        return Result.success();
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //设置查询条件，查询当前登录用户的购物车数据
        shoppingCart.setUserId(MyThreadLocal.getCurrentId());

        List<ShoppingCart> list = shopCartMapper.list(shoppingCart);

        if(list != null && list.size() > 0){
            shoppingCart = list.get(0);

            Integer number = shoppingCart.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shopCartMapper.deleteById(shoppingCart.getId());
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shopCartMapper.updateNumberById(shoppingCart);
            }
        }
    }
}
