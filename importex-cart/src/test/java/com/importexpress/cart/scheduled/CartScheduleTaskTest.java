package com.importexpress.cart.scheduled;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Author jack.luo
 * @create 2020/6/12 13:21
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CartScheduleTaskTest {

    @Autowired
    private CartScheduleTask task;

    @Test
    public void saveAllCartsToFiles() throws IOException {
        task.saveAllCartsToFiles();
    }

    @Test
    public void readFileToBean() throws IOException {
        task.readFileToBean();
    }

    @Test
    public void decompress() throws IOException {
        task.decompress();
    }



}