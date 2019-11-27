package com.importexpress.user.service.impl;


import com.importexpress.user.mapper.UserMapper;
import com.importexpress.user.pojo.UserBean;
import com.importexpress.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public int updateUserShopifyFlag(int userId, String shopifyName) {
        return userMapper.updateUserShopifyFlag(userId, shopifyName);
    }

    @Override
    public UserBean getUserByShopifyName(String shopifyName) {
        return userMapper.getUserByShopifyName(shopifyName);
    }
}
