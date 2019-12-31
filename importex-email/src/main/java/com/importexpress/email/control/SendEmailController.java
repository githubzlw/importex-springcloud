package com.importexpress.email.control;

import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.pojo.MailBean;
import com.importexpress.email.service.SendMailFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api("发送邮件接口")
@RestController
@RequestMapping("/sendMail")
public class SendEmailController {

    private final SendMailFactory sendMailFactory;

    public SendEmailController(SendMailFactory sendMailFactory) {
        this.sendMailFactory = sendMailFactory;
    }

    @ApiOperation("根据mailBean发送邮件")
    @PostMapping("/mailBean")
    public CommonResult sendEmailByMailBean(@RequestBody MailBean mailBean) {

        Assert.notNull(mailBean.getTo(), "邮箱异常");
        Assert.notNull(mailBean.getBody(), "邮件内容异常");
        try {
            mailBean.setTest(true);
            sendMailFactory.sendMail(mailBean);
            return CommonResult.success("send to " + mailBean.getTo() + " success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sendEmailByMailBean,send email to[{}],error", mailBean, e);
            return CommonResult.failed(e.getMessage());
        }

    }


}