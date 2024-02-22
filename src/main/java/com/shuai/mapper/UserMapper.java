package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User>{

    @Select("select nickname,avatar from user where id = #{userId}")
    User getBriefInfo(@Param("userId") Long userId);

    @Select("select * from user_role where role_id = 1 and user_id =#{id}")
    Integer isAdmin(@Param("id") Long id);
    @Select("select * from user where id =#{id} and status = '正常'")
    Integer isNormal(@Param("id") Long id);

    @Update("update user set status = '禁用' where id=#{id} and status = '正常'")
    Integer banById(@Param("id") Long id);

    @Update("update user set status = '正常' where id=#{id} and status = '禁用'")
    Integer unlockById(@Param("id") Long id);

    @Select("select r.id from role r where r.status='正常' and r.id in ( " +
            "select role_id from user_role ur where user_id=#{id})")
    List<Integer> roleIsNormal(Long id);

    @Insert("insert into orange.user_role values (#{userId},#{roleId})")
    Integer advanceUser(@Param("userId") Long userId, @Param("roleId") Integer roleId);

    @Select("select * from user_role where user_id=#{userId} and role_id=#{roleId}")
    Integer isOk(@Param("userId") Long userId,@Param("roleId") Integer roleId);

    @Delete("delete from user_role where user_id=#{userId} and role_id=#{roleId}")
    Integer demoteUser(@Param("userId") Long userId,@Param("roleId") Integer roleId);
}

