package com.zishanshu.service;

import com.zishanshu.domain.User;

public interface UserService {
    User getUserById(Integer id);

    Integer insertUser(User user);
}
