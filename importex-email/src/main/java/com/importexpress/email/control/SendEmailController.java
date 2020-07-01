//package com.importexpress.email.control;
//
//import com.importexpress.comm.domain.CommonResult;
//import com.importexpress.comm.pojo.MailBean;
//import com.importexpress.comm.pojo.MailTemplateBean;
//import com.importexpress.email.service.TemplateMailService;
//import com.importexpress.email.service.SendMailFactory;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.Assert;
//import org.springframework.web.bind.annotation.*;
//
//@Slf4j
//@Api("发送邮件接口")
//@RestController
//public class SendEmailController {
//
//    private final SendMailFactory sendMailFactory;
//
//    private final TemplateMailService templateMailService;
//
//    public SendEmailController(SendMailFactory sendMailFactory, TemplateMailService templateMailService) {
//        this.sendMailFactory = sendMailFactory;
//        this.templateMailService = templateMailService;
//    }
//
//    @ApiOperation("根据mailBean发送邮件")
//    @PostMapping("/sendEmail")
//    public CommonResult sendEmail(@RequestBody MailTemplateBean mailTemplateBean) {
//
//        try {
//            sendMailFactory.sendMail(templateMailService.processTemplate(mailTemplateBean));
//            return CommonResult.success("send to " + mailTemplateBean.getMailBean().getTo() + " successful.");
//        } catch (Exception e) {
//            log.error("sendEmailByMailBean,send email to[{}],error", mailTemplateBean, e);
//            return CommonResult.failed(e.getMessage());
//        }
//    }
//
////
////    /**
////     * @param mailBean
////     * @return
////     */
////    @ApiOperation("根据mailBean发送邮件")
////    @PostMapping("/templateType")
////    public CommonResult sendEmailByBodyTemplateType(@RequestBody MailBean mailBean) {
////        Assert.notNull(mailBean.getTo(), "邮箱异常");
////        Assert.notNull(mailBean.getTemplateType(), "邮件内容异常");
////        Assert.notNull(mailBean.getSiteEnum(), "网站异常");
////        try {
////            mailBean.setTest(true);
////            sendMailFactory.sendMail(mailBean);
////            return CommonResult.success("send to " + mailBean.getTo() + " success");
////        } catch (Exception e) {
////            e.printStackTrace();
////            log.error("templateType,send email to[{}],error", mailBean, e);
////            return CommonResult.failed(e.getMessage());
////        }
////    }
////
//
////    @PostMapping("/genWelcomeBodyAndSend")
////    public CommonResult genWelcomeBodyAndSend(@RequestParam(name = "email", required = true) String email,
////                                              @RequestParam(name = "name", required = true) String name,
////                                              @RequestParam(name = "pass", required = true) String pass,
////                                              @RequestParam(name = "from", required = true) String from,
////                                              @RequestParam(name = "siteEnum", required = true) SiteEnum siteEnum) {
////        try {
////            sendEmailService.genWelcomeBodyAndSend(email, name, pass, from, siteEnum);
////            return CommonResult.success("genWelcomeBodyAndSend send to " + email + " success");
////        } catch (Exception e) {
////            e.printStackTrace();
////            log.error("genWelcomeBodyAndSend,send email[{}],name[{}],pass[{}],from[{}],siteEnum[{}],error", email, name,
////                    pass, from, siteEnum, e);
////            return CommonResult.failed(e.getMessage());
////        }
////    }
//
////    @PostMapping("/genReceivedBodyAndSend")
////    public CommonResult genReceivedBodyAndSend(@RequestParam(name = "orderNo", required = true) String orderNo,
////                                               @RequestParam(name = "userId", required = true) Integer userId,
////                                               @RequestParam(name = "siteEnum", required = true) SiteEnum siteEnum) {
////        Assert.notNull(userId, "userId 异常");
////        try {
////            sendEmailService.genReceivedBodyAndSend(orderNo, userId, siteEnum);
////            return CommonResult.success("genReceivedBodyAndSend send to " + userId + " success");
////        } catch (Exception e) {
////            e.printStackTrace();
////            log.error("genReceivedBodyAndSend,send orderNo[{}],userId[{}],siteEnum[{}],error", orderNo, userId, siteEnum, e);
////            return CommonResult.failed(e.getMessage());
////        }
////    }
////
////    @PostMapping("/genNewPasswordBodyAndSend")
////    public CommonResult genNewPasswordBodyAndSend(@RequestParam(name = "email", required = true) String email,
////                                                  @RequestParam(name = "passWord", required = true) String passWord,
////                                                  @RequestParam(name = "businessName", required = true) String businessName,
////                                                  @RequestParam(name = "businessIntroduction", required = true) String businessIntroduction,
////                                                  @RequestParam(name = "siteEnum", required = true) SiteEnum siteEnum) {
////        try {
////            sendEmailService.genNewPasswordBodyAndSend(email, passWord, businessName, businessIntroduction, siteEnum);
////            return CommonResult.success("genNewPasswordBodyAndSend send to " + email + " success");
////        } catch (Exception e) {
////            e.printStackTrace();
////            log.error("genNewPasswordBodyAndSend,send email[{}],passWord[{}],businessName[{}],businessIntroduction[{}],siteEnum[{}],error", email, passWord, businessName, businessIntroduction, siteEnum, e);
////            return CommonResult.failed(e.getMessage());
////        }
////    }
////
////    @PostMapping("/genActivationBodyAndSend")
////    public CommonResult genActivationBodyAndSend(@RequestParam(name = "email", required = true) String email,
////                                                 @RequestParam(name = "name", required = true) String name,
////                                                 @RequestParam(name = "pass", required = true) String pass,
////                                                 @RequestParam(name = "fromWhere", required = true) String fromWhere,
////                                                 @RequestParam(name = "siteEnum", required = true) SiteEnum siteEnum) {
////        try {
////            sendEmailService.genActivationBodyAndSend(email, name, pass, fromWhere, siteEnum);
////            return CommonResult.success("genActivationBodyAndSend send to " + email + " success");
////        } catch (Exception e) {
////            e.printStackTrace();
////            log.error("genActivationBodyAndSend,send email[{}],name[{}],pass[{}],fromWhere[{}],siteEnum[{}],error", email, name, pass, fromWhere, siteEnum, e);
////            return CommonResult.failed(e.getMessage());
////        }
////    }
////
////
////    @PostMapping("/genAccountUpdateBodyAndSend")
////    public CommonResult genAccountUpdateBodyAndSend(@RequestParam(name = "email", required = true) String email,
////                                                    @RequestParam(name = "siteEnum", required = true) SiteEnum siteEnum) {
////        try {
////            sendEmailService.genAccountUpdateBodyAndSend(email, siteEnum);
////            return CommonResult.success("genAccountUpdateBodyAndSend send to " + email + " success");
////        } catch (Exception e) {
////            e.printStackTrace();
////            log.error("genAccountUpdateBodyAndSend,send email[{}],siteEnum[{}],error", email, siteEnum, e);
////            return CommonResult.failed(e.getMessage());
////        }
////    }
////
////    @PostMapping("/justSend")
////    public CommonResult justSend(@RequestParam(name = "toEmail", required = true) String toEmail,
////                                 @RequestParam(name = "content", required = true) String content,
////                                 @RequestParam(name = "title", required = true) String title,
////                                 @RequestParam(name = "siteEnum", required = true) SiteEnum siteEnum) {
////        try {
////            sendEmailService.justSend(toEmail, content, title, siteEnum);
////            return CommonResult.success("genAccountUpdateBodyAndSend send to " + toEmail + " success");
////        } catch (Exception e) {
////            e.printStackTrace();
////            log.error("justSend,send email[{}],content[{}],title[{}],siteEnum[{}],error", toEmail, content, title, siteEnum, e);
////            return CommonResult.failed(e.getMessage());
////        }
////    }
//
//}