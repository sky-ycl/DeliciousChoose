package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜品管理
 */

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关的接口")
public class DishController {

    @Resource
    private DishService dishService;

    @PostMapping()
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }


    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        return dishService.pageQuery(dishPageQueryDTO);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品的状态")
    public Result status(@RequestParam("id") Long id,@PathVariable("status") Integer status){
        return dishService.updateStatus(id,status);
    }


    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result deleteDish(@RequestParam("ids") List<Long> ids){
        return dishService.deleteDish(ids);
    }
}
