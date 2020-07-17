package com.importexpress.serialport.service.impl;


import com.importexpress.serialport.exception.SerialPortException;
import com.importexpress.serialport.service.SerialPortService;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @Author jack.luo
 * @create 2020/5/21 11:09
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SerialPortServiceTest {

    public static final String GOODS_ID = "123456789";
    @Autowired
    private SerialPortService serialPortService;

    @Test
    public void testMoveGoods()  {

        IntStream.range(0,99).forEach( i -> {
            try {
                System.out.println(i);
                serialPortService.moveGoods(2000,2000,1000,GOODS_ID);
            } catch (Exception e) {
                Assert.isTrue(false);
            }
        });
    }

    @Test
    public void test() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        serialPortService.moveGoods(2000,2000,1000,GOODS_ID);
        System.out.println("aaa");
    }

    @Test
    public void findGoodsByGrid() throws PortInUseException, UnsupportedCommOperationException, NoSuchPortException, InterruptedException, IOException {

        Map<String, String> hmGoods = new HashMap<>();
        serialPortService.moveGoodsByFinder(hmGoods);
    }

    @Test
    public void returnMoveGoodsByFinder() throws PortInUseException, UnsupportedCommOperationException, NoSuchPortException, InterruptedException, IOException {

        serialPortService.returnMoveGoodsByFinder("1", "2-1", GOODS_ID);
    }


}