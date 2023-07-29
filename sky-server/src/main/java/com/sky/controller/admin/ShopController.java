package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.sky.constant.RedisConstant.SHOP_STATUS;
import static com.sky.constant.RedisConstant.SHOP_STATUS_KEY;

@RestController("adminShopUserController")
@Api(tags = "店铺相关接口")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置店铺的状态
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺的状态")
    public Result setShopStatus(@PathVariable("status") Integer status){
        stringRedisTemplate.opsForValue().set(SHOP_STATUS_KEY,status.toString());
        return Result.success();
    }

    /**
     * 获取店铺的状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的状态")
    public Result getShopStatus(){
        String status = stringRedisTemplate.opsForValue().get(SHOP_STATUS_KEY);
        return Result.success(status);
    }
}
