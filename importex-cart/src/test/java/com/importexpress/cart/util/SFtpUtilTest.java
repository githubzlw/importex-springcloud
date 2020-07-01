package com.importexpress.cart.util;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Author jack.luo
 * @create 2020/6/15 17:59
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SFtpUtilTest {

    @Autowired
    private SFtpUtil sFtpUtil;

    @Test
    public void uploadFile() throws IOException {
        sFtpUtil.uploadFile("d:\\p_carts_20200612.json");
    }


}