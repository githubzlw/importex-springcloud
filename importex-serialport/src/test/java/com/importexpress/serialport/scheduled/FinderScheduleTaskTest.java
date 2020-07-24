package com.importexpress.serialport.scheduled;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Author jack.luo
 * @create 2020/6/29 16:48
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FinderScheduleTaskTest {

    @Autowired
    private FinderScheduleTask finderScheduleTask;

    @Test
    public void saveFinderToFiles() throws IOException {
        finderScheduleTask.saveFinderToFiles();
    }
}