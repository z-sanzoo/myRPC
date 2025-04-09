package com.zishanshu.service;

import com.zishanshu.domain.Blog;

public interface BlogService {
    Blog getBlogById(Integer id);

    Integer insertBlog(Blog blog);

    Integer deleteBlogById(Integer id);

    Integer updateBlog(Blog blog);
}
