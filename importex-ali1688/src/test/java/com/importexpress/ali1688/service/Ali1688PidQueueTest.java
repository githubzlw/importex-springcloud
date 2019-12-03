package com.importexpress.ali1688.service;


import com.importexpress.ali1688.model.PidQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ali1688PidQueueTest {

    @Autowired
    private Ali1688Service ali1688Service;

    @Test
    public void pushPid() {
        IntStream.range(1, 100).forEach(i -> ali1688Service.pushPid("shop002", i));
    }

    @Test
    public void updatePidQueue() {

        IntStream.range(1, 50).forEach(i -> ali1688Service.updatePidQueue(i, 1));
    }


    @Test
    public void getAllUnStartPids() {
        List<PidQueue> allUnStartPids = ali1688Service.getAllUnStartPids();
        System.out.println(allUnStartPids);
    }

    @Test
    public void getAllPids() {
        List<PidQueue> allUnStartPids = ali1688Service.getAllPids(1, 20);
        System.out.println(allUnStartPids);

        allUnStartPids = ali1688Service.getAllPids(3, 20);
        System.out.println(allUnStartPids);
    }


}
