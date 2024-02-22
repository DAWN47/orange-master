package com.shuai;

import com.shuai.mapper.PostMapper;
import com.shuai.mapper.UserMapper;
import com.shuai.pojo.vo.UserVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrangeApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {
//        postMapper.selectById("120230808143833");
//        Post post = postMapper.selectOne(new LambdaQueryWrapper<Post>().eq(Post::getId, "120230808143833"));
//        String tolke ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZXhwIjoxNjk2MzM0Mzc4fQ.QqmCtgBBgG-fgkq5SYSBiOOgG_QOL7NGUnFddTWqKeY";
//        System.out.println(JwtUtil.verify(tolke));
//        System.out.println(JwtUtil.isExpire(tolke));
        UserVo userVo=new UserVo();
        userVo.setId(2l);
        System.out.println(userMapper.isAdmin(userVo.getId()));
    }

}
