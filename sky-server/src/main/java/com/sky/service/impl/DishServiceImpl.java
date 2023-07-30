package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 向数据库插入数据
        dishMapper.insert(dish);

        // 获取insert语句生成的主键值
        Long dishId = dish.getId();
        //向口味表插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null || flavors.size() > 0) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        // 分页查询
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return Result.success(pageResult);
    }

    /**
     * 修改菜品的状态 0 停售 1 起售
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public Result updateStatus(Long id, Integer status) {
        int count = dishMapper.updateStatus(id, status);
        if (count > 0) {
            return Result.success();
        }
        return Result.error("修改失败");
    }

    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public Result deleteDish(List<Long> ids) {
        try {
            // 判断删除的菜品是否是起售中
            // 判断删除的菜品是否被套餐关联
            if (isSale(ids) && isAssociate(ids)) {
                // 删除菜品后 口味也要删除
                ids.forEach(id->{
                    dishMapper.deleteByIds(ids);
                    dishFlavorMapper.deleteByDishIds(ids);
                });
            }
        } catch (DeletionNotAllowedException e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success();
    }

    //判断菜品是否是起售状态
    public boolean isSale(List<Long> ids) {
        ids.forEach(id -> {
            Dish dish = dishMapper.selectDishStatus(id);
            if (dish.getStatus() == 1) { // 菜品属于起售状态
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        return true;
    }

    //判断菜品是否被套餐关联
    public boolean isAssociate(List<Long> ids) {
        ids.forEach(dishId -> {
            List<Long> setMealIds = setMealDishMapper.getSetmealIdsByDishIds(ids);
            if (setMealIds != null || setMealIds.size() > 0) {
            }
        });
        return true;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
}
