package com.zishanshu.service.Impl;

import com.zishanshu.domain.Blog;
import com.zishanshu.service.BlogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlogServiceImpl implements BlogService {

    @Override
    public Blog getBlogById(Integer id) {
        //随机抛出异常
        if (Math.random() > 0.5) {
            throw new RuntimeException("服务调用异常");
        }
        Blog blog = Blog.builder().id(id).title("我的博客").userId(22).build();
        log.debug("BlogService服务调用"+id);
        return blog;
    }

    @Override
    public Integer insertBlog(Blog blog) {
        return 0;
    }

    @Override
    public Integer deleteBlogById(Integer id) {
        return 0;
    }

    @Override
    public Integer updateBlog(Blog blog) {
        return 0;
    }
}
