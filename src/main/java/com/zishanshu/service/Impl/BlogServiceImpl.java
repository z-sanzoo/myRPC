package com.zishanshu.service.Impl;

import com.zishanshu.domain.Blog;
import com.zishanshu.service.BlogService;

public class BlogServiceImpl implements BlogService {

    @Override
    public Blog getBlogById(Integer id) {
        Blog blog = Blog.builder().id(id).title("我的博客").userId(22).build();
        System.out.println("客户端查询了"+id+"博客");
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
