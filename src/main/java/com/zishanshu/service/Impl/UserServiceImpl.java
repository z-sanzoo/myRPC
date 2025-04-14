package com.zishanshu.service.Impl;

import com.zishanshu.service.UserService;
import com.zishanshu.domain.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
@Slf4j
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Integer id) {
        log.debug("getUserById服务调用");
        Random random = new Random();
        return User.builder().name("zishanshu" + random.nextInt(100)).id(id).sex(random.nextBoolean()).build();
    }

    @Override
    public Integer insertUser(User user) {
        log.debug("insertUser服务调用");
        return 1;
    }
}
