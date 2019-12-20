package com.importexpress.email.service;


import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author jack.luo
 * @create 2019/12/20 9:43
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SendMailFactoryTest {

    @Autowired
    private SendMailFactory sendMailFactory;

    @Test
    public void sendMail() {
        String body = String.join(
                System.getProperty("line.separator"),
                "<h1>Amazon SES SMTP Email Test</h1>",
                "<p>This email was sent with Amazon SES using the ",
                "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
                " for <a href='https://www.java.com'>Java</a>."
        );
        MailBean mailBean = new MailBean();
        mailBean.setTo("luohao518@yeah.net");
        mailBean.setSubject("This is a test email");
        mailBean.setSiteEnum(SiteEnum.KIDS);
        mailBean.setBody(body);
        mailBean.setTest(false);
        sendMailFactory.sendMail(mailBean);
    }

    //    public static void main(String[] args) {
//        try {
//            final String BODY = String.join(
//                    System.getProperty("line.separator"),
//                    "<h1>Amazon SES SMTP Email Test</h1>",
//                    "<p>This email was sent with Amazon SES using the ",
//                    "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
//                    " for <a href='https://www.java.com'>Java</a>."
//            );
//            new SendMailByAmazon().sendMail("luohao518@163.com", "", "Amazon SES test (SMTP interface accessed using Java)", BODY);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Test
//    public void sendMail() {
//
//        Map<String, Object> model =new HashMap<>(9);
//        model.put("logoUrl","logoUrl");
//        model.put("name", "name");
//        model.put("email", "luohao518@yeah.net");
//        model.put("pass", "pass");
//        model.put("activeLink", "activeLink");
//        model.put("here", "here");
//
//        sendMailFactory.sendMail("luohao518@yeah.net", null, "You've successfully created an ChinaWholesaleInc account. We Welcome You!", model, TemplateType.WELCOME);
//
//    }
//
//    @Test
//    public void sendActivationMail() {
//
//        String activeLink = PropertyUtils.getValueFromResourceFile("appConfig.path")+"userController/upUserState?code=xxxxx&email=test@gmail.com&from=sz";
//        String here	 = PropertyUtils.getValueFromResourceFile("appConfig.path") + "individual/getCenter";
//        Map<String, Object> model =new HashMap<>();
//        int site = MultiSiteUtil.site;
//        model.put("logoUrl",site);
//        model.put("name", "name1");
//        model.put("email", "test@gmail.com");
//        model.put("pass", "pass1");
//        model.put("activeLink", activeLink);
//        model.put("here", here);
//
//        sendMailFactory.sendMail("luohao518@yeah.net", null, "You've successfully created an ImportExpress account. We Welcome You!", model, TemplateType.ACTIVATION);
//
//    }
//    @Test
//    public void sendReciveOrderEmailTest(){
//        String orderNo = "QC05399885429765";
//        int userid = 1019075;
//        int i = emailService.sendEmail(orderNo, userid);
//    }


}
