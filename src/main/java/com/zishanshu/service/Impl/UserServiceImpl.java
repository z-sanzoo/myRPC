package com.zishanshu.service.Impl;

import com.zishanshu.service.UserService;
import com.zishanshu.domain.User;

import java.util.Random;

public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Integer id) {
        System.out.println("getUserById服务调用");
        Random random = new Random();
        return User.builder().name("zishanshu" + random.nextInt(100)).id(id).sex(random.nextBoolean()).build();
    }

    @Override
    public Integer insertUser(User user) {
        System.out.println("insertUser服务调用");
        return 1;
    }
}
