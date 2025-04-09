package com.zishanshu.client;

import com.zishanshu.domain.Blog;
import com.zishanshu.domain.User;
import com.zishanshu.service.BlogService;
import com.zishanshu.service.Impl.UserServiceImpl;
import com.zishanshu.service.UserService;

public class RPCClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1",8080);
        UserService userService = clientProxy.getProxy(UserService.class);
        BlogService blogService = clientProxy.getProxy(BlogService.class);

//        System.out.println(UserServiceImpl.class.getName());

        User user = userService.getUserById(1);
        System.out.println("服务端返回对象"+user);

        Integer i = userService.insertUser(user);
        System.out.println("服务端返回对象"+i);

        Blog blog = blogService.getBlogById(1);
        System.out.println(blog);

    }
}
