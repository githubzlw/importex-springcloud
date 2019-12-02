package com.importexpress.ali1688.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ali1688PidQueueTest {

    @Autowired
    private Ali1688Service ali1688Service;

    @Test
    public void pushPid() {

        ali1688Service.pushPid("111",123);
        ali1688Service.pushPid("111",321);

    }


}
