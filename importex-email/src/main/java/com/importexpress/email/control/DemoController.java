package com.importexpress.email.control;

import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.mq.SendMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@Slf4j
public class DemoController {

    private final SendMQ sender;

    public DemoController(SendMQ sender) {
        this.sender = sender;
    }

    @GetMapping("/mailtest")
    public String demo(String str) {
        log.info("step into the demo(),input:[{}]", str);
        int count = 10;
        sendMQTtest(count);
        return "test finish,count " + count;
    }

    private void sendMQTtest(int count) {

        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSubject("This is a ACTIVATION email");
        mailBean.setSiteEnum(SiteEnum.IMPORTX);
        mailBean.setTest(true);
        Map<String, Object> model = new HashMap<>();
        model.put("logoUrl", SiteEnum.IMPORTX.getUrl());
        model.put("name", "name1");
        model.put("email", "test@gmail.com");
        model.put("pass", "pass1");
        model.put("activeLink", "activeLink......");
        model.put("here", "here");
        mailBean.setModel(model);
        mailBean.setTemplateType(TemplateType.ACTIVATION);
        IntStream.range(1, count).forEach(i -> {
            sender.sendMQToMail(mailBean);
//            try {
//                Thread.currentThread().sleep(100);
//            } catch (InterruptedException e) {
//
//            }
        });
    }
}
