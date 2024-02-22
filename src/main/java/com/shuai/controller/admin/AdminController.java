package com.shuai.controller.admin;

import com.shuai.handler.UserThreadLocal;
import com.shuai.pojo.po.Post;
import com.shuai.pojo.po.Role;
import com.shuai.pojo.vo.GoodVo;
import com.shuai.pojo.vo.PostVo;
import com.shuai.pojo.vo.UserVo;
import com.shuai.service.GoodService;
import com.shuai.service.PostService;
import com.shuai.service.RoleService;
import com.shuai.service.UserService;
import com.shuai.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @Author: fengxin
 * @CreateTime: 2023-08-13  07:51
 * @Description: 后台管理器
 */
@Slf4j
@RestController
@Transactional
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GoodService goodService;

    /**
     * @description: 删除指定帖子，及其相关数据
     * @author: fengxin
     * @date: 2023/9/26 19:47
     * @param: [postId]
     * @return: 是否成功
     **/
    @PreAuthorize("hasAuthority('/admin/post/delete')")
    @DeleteMapping("/post/delete")
    public Result delete(@RequestBody PostVo postVo) {
        log.info("传入：{}",postVo.getPostId());
        // 0. 拿到当前用户id
        Long userId = UserThreadLocal.get().getId();
        // 1. 通过 postId 查询数据库,拿到指定帖子信息
        Post post = postService.getById(postVo.getPostId());
        // 2. 判断帖子是否存在
        if (Objects.equals(post,null)) {
            return Result.fail("删除失败，指定帖子不存在！");
        }
        // 3. 删除指定帖子
        return postService.delete(postVo.getPostId());
    }

    /**
     * @param goodVo:
     * @return Result
     * @author DAWN
     * @description TODO 强制下架货品
     * @date 2024/2/20 20:46
     */
    @PreAuthorize("hasAuthority('/admin/good/delete')")
    @DeleteMapping("/good/delete")
    public Result goodDelete(@RequestBody GoodVo goodVo) {
        return goodService.forceDeleteGood(goodVo.getGoodId());
    }



    /**
 * @param uservo:
 * @return 用户信息
 * @author DAWN
 * @description TODO 管理员登入
 * @date 2024/2/20 20:41
 */
    @PostMapping("/login")
    public Result adminLogin(@RequestBody UserVo uservo){
        return userService.adminLogin(uservo);
    }

    /**
     * @param userVo:
     * @return 是否成功
     * @author DAWN
     * @description TODO 封禁用户
     * @date 2024/2/20 20:42
     */
    @PreAuthorize("hasAuthority('/admin/ban/id')")
    @PostMapping("/ban/id")
    public Result banById(@RequestBody UserVo userVo){
        Long target=userVo.getId();
        if(target==null){
            return Result.fail("封禁失败");
        }
        return userService.banById(target);
    }

    /**
     * @param userVo:
     * @return 是否成功
     * @author DAWN
     * @description TODO 解禁用户
     * @date 2024/2/20 20:42
     */
    @PreAuthorize("hasAuthority('/admin/unlock/id')")
    @PostMapping("/unlock/id")
    public Result unlockById(@RequestBody UserVo userVo){
        Long target=userVo.getId();
        if(target==null){
            return Result.fail("解禁失败");
        }
        return userService.unlockById(target);
    }
//忽略 权限信息登入时已传给前端
//    //查询用户权限
//    @PreAuthorize("hasAuthority('/admin/select/permission/id')")
//    @PostMapping("/select/permission/id")
//    public Result selectPermission(@RequestBody UserVo userVo){
//        Long target = userVo.getId();
//        if(target==null){
//            return Result.fail("查询失败");
//        }
//        return userService.selectPermissionById(target);
//    }

    /**
     * @param role:
     * @return 是否成功
     * @author DAWN
     * @description TODO 封禁角色
     * @date 2024/2/20 20:42
     */
    @PreAuthorize("hasAuthority('/admin/ban/role/id')")
    @PostMapping("/ban/role/id")
    public Result banByRoleId(@RequestBody Role role){
        Long id = role.getId();
        if(id==null){
            return Result.fail("封禁角色失败");
        }
        return roleService.banRoleById(id);
    }


    /**
     * @param role:
     * @return 是否成功
     * @author DAWN
     * @description TODO 解禁角色
     * @date 2024/2/20 20:43
     */
    @PreAuthorize("hasAuthority('/admin/unlock/role/id')")
    @PostMapping("/unlock/role/id")
    public Result unlockByRoleId(@RequestBody Role role){
        Long id = role.getId();
        if(id==null){
            return Result.fail("解禁角色失败");
        }
        return roleService.unlockRoleById(id);
    }

    /**
     * @param userVo:
     * @return Result
     * @author DAWN
     * @description TODO 用户==>商家
     * @date 2024/2/20 21:23
     */
    @PreAuthorize("hasAuthority('/admin/user/to/business')")
    @PostMapping("/user/to/business")
    public Result userToBusiness(@RequestBody UserVo userVo){
        Long id = userVo.getId();
        if(id==null){
            return Result.fail("用户升级为商家失败");
        }
        return userService.userToBusiness(id);
    }

    /**
     * @param userVo:
     * @return Result
     * @author DAWN
     * @description TODO 商家==>用户
     * @date 2024/2/20 21:23
     */
    @PreAuthorize("hasAuthority('/admin/business/to/user')")
    @DeleteMapping("/business/to/user")
    public Result businessToUser(@RequestBody UserVo userVo){
        Long id = userVo.getId();
        if(id==null){
            return Result.fail("商家降级为用户失败");
        }
        return userService.businessToUser(id);
    }


    /**
     * @param userVo:
     * @return Result
     * @author DAWN
     * @description TODO 用户==>专家
     * @date 2024/2/20 21:23
     */
    @PreAuthorize("hasAuthority('/admin/user/to/expert')")
    @PostMapping("/user/to/expert")
    public Result userToExpert(@RequestBody UserVo userVo){
        Long id = userVo.getId();
        if(id==null){
            return Result.fail("用户升级为专家失败");
        }
        return userService.userToExpert(id);
    }

    /**
     * @param userVo:
     * @return Result
     * @author DAWN
     * @description TODO 专家==>用户
     * @date 2024/2/20 21:23
     */
    @PreAuthorize("hasAuthority('/admin/expert/to/user')")
    @DeleteMapping("/expert/to/user")
    public Result expertToUser(@RequestBody UserVo userVo){
        Long id = userVo.getId();
        if(id==null){
            return Result.fail("专家降级为用户失败");
        }
        return userService.expertToUser(id);
    }

    /**
     * @param userVo:
     * @return Result
     * @author DAWN
     * @description TODO 用户==>管理员
     * @date 2024/2/20 21:23
     */
    @PreAuthorize("hasAuthority('/admin/user/to/admin')")
    @PostMapping("/user/to/admin")
    public Result userToAdmin(@RequestBody UserVo userVo){
        Long id = userVo.getId();
        if(id==null){
            return Result.fail("用户升级为管理员失败");
        }
        return userService.userToAdmin(id);
    }

    /**
     * @param userVo:
     * @return Result
     * @author DAWN
     * @description TODO 管理员==>用户
     * @date 2024/2/20 21:23
     */
    @PreAuthorize("hasAuthority('/admin/admin/to/user')")
    @DeleteMapping("/admin/to/user")
    public Result adminToUser(@RequestBody UserVo userVo){
        Long id = userVo.getId();
        if(id==null){
            return Result.fail("管理员降级为用户失败");
        }
        return userService.adminToUser(id);
    }

    /**
     * @param userVo:
     * @return Result
     * @author DAWN
     * @description TODO 禁言用户
     * @date 2024/2/22 21:43
     */
    @PreAuthorize("hasAuthority('/admin/ban/user/post')")
    @PostMapping("/ban/user/post")
    public Result banUserPost(@RequestBody UserVo userVo){
        Long id = userVo.getId();
        if(id==null){
            return Result.fail("禁言用户失败");
        }
        return userService.banUserPost(id);
    }

    /**
     * @param userVo:
     * @return Result
     * @author DAWN
     * @description TODO 解除用户禁言
     * @date 2024/2/22 21:44
     */
    @PreAuthorize("hasAuthority('/admin/unlock/user/post')")
    @PostMapping("/unlock/user/post")
    public Result unlockUserPost(@RequestBody UserVo userVo){
        Long id = userVo.getId();
        if(id==null){
            return Result.fail("用户解除禁言失败");
        }
        return userService.unlockUserPost(id);
    }

    /**
     * @param :
     * @return Result
     * @author DAWN
     * @description TODO 查询所有用户
     * @date 2024/2/22 21:44
     */
    @PreAuthorize("hasAuthority('/admin/select/all/user')")
    @PostMapping("/select/all/user")
    public Result selectAllUser(){
        return userService.selectAllUser();
    }

}
