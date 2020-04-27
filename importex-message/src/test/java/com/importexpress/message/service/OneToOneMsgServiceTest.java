package com.importexpress.message.service;


import com.importexpress.comm.pojo.SiteEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @Author jack.luo
 * @create 2020/4/27 16:26
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OneToOneMsgServiceTest {


    @Autowired
    private OneToOneMsgService messageService;

    /**
     * addCart
     */
    @Test
    public void sendMsg() {
        messageService.sendMsg(SiteEnum.KIDS,10001,10002,"hello world1");
        messageService.sendMsg(SiteEnum.KIDS,10001,10002,"hello world2");
        messageService.sendMsg(SiteEnum.KIDS,10001,10002,"hello world3");
    }

    @Test
    public void readMessage1() {
        messageService.sendMsg(SiteEnum.KIDS,10001,10002,"hello world1");
        Assert.isTrue(messageService.readMsg(SiteEnum.KIDS, 10002).size()>0);
    }

    @Test
    public void readMessage2() {
        System.out.println(messageService.readMsg(SiteEnum.KIDS, 10001));
    }

}