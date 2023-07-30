package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.sky.constant.RedisConstant.SHOP_STATUS_KEY;

@RestController("userShopController")
@Api(tags = "店铺相关接口")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


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
