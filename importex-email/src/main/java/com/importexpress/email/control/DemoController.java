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

        Map<String, Object> model = new HashMap<>();
        model.put("logoUrl", SiteEnum.IMPORTX.getUrl());
        model.put("name", "name1");
        model.put("email", "test@gmail.com");
        model.put("pass", "pass1");
        model.put("activeLink", "activeLink......");
        model.put("here", "here");

         MailBean mailBean = MailBean.builder().to("luohao518@yeah.net").subject("This is a ACTIVATION email").siteEnum(SiteEnum.IMPORTX)
                .model(model).templateType(TemplateType.ACTIVATION).isTest(true).build();
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
