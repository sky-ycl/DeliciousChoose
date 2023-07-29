package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 修改菜品的状态 0 停售 1 起售
     * @param id
     * @param status
     * @return
     */
    Result updateStatus(Long id, Integer status);

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    Result deleteDish(List<Long> ids);
}
