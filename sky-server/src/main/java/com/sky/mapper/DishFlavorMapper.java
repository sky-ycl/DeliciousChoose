package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insertBatch(List<DishFlavor> flavors);

    /**
     * 通过菜品id来删除口味
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);


    /**
     * 通过菜品id来查询口味
     * @param id
     * @return
     */
    List<DishFlavor> getByDishId(Long id);
}
