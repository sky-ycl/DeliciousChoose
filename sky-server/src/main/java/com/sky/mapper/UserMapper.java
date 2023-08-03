package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openid来查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据id来查询用户
     * @param userId
     * @return
     */
    @Select("select * from user where id=#{userId}")
    User getById(Long userId);

    /**
     * 统计某个时间段的用户数量
     * @param map
     * @return
     */
    Integer countUserNumByMap(Map<String, Object> map);
}
