package com.importexpress.shopify.service.impl;

import com.importexpress.shopify.mapper.UserMapper;
import com.importexpress.shopify.service.UserService;
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
}
