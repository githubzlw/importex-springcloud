package com.importexpress.serialport.service.impl;


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

import java.util.stream.IntStream;

/**
 * @Author jack.luo
 * @create 2020/5/21 11:09
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SerialPortServiceImplTest{

    @Autowired
    private SerialPortService serialPortService;

    @Test
    public void testMoveGoods()  {

        IntStream.range(0,99).forEach( i -> {
            try {
                System.out.println(i);
                serialPortService.moveGoods(2000,2000,1000);
            } catch (Exception e) {
                Assert.isTrue(false);
            }
        });
    }

    @Test
    public void test() throws PortInUseException, NoSuchPortException, InterruptedException, UnsupportedCommOperationException {

        serialPortService.moveGoods(2000,2000,1000);
        System.out.println("aaa");
    }
}