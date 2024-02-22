package com.shuai.pojo.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.shuai.pojo.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: fengxin
 * @CreateTime: 2023-04-19  18:56
 * @Description: 用于用户登录、身份验证
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo implements UserDetails {

    // 主键
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 昵称
    private String nickname;

    // 性别
    private String sex;

    // 头像
    private String avatar;

    // 电话号码
    private String phone;

    //微信唯一标识
    private String openid;

    // 联合id
    private String wxUnionId;

    // token令牌
    private String token;

    // 验证码
    private String code;

    public void from(WxUserInfo wxUserInfo){
        this.nickname = wxUserInfo.getNickName();
        this.avatar = wxUserInfo.getAvatarUrl();
        this.username = "";
        this.password = "";
        this.sex = wxUserInfo.getGender();
        this.openid = wxUserInfo.getOpenid();
        this.wxUnionId = wxUserInfo.getUnionId();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }


    private List<String> permissions;

    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(authorities != null){
            return authorities;
        }
        //把permissions中String类型的权限信息封装成SimpleGrantedAuthority对象
//       authorities = new ArrayList<>();
//        for (String permission : permissions) {
//            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permission);
//            authorities.add(authority);
//        }
        authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return authorities;
//        return null;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
