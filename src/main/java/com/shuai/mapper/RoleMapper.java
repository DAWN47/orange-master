package com.shuai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shuai.pojo.po.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Update("update role set status = '停用' where id=#{id} and status='正常'")
    Integer banRoleById(Long id);

    @Update("update role set status='正常' where id=#{id} and status='停用'")
    Integer unlockRoleById(Long id);
}
