package com.importexpress.email.service;

import com.alibaba.fastjson.JSONArray;
import com.importexpress.comm.pojo.MailBean;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.comm.pojo.TemplateType;
import com.importexpress.email.vo.ShopCarMarketing;
import com.importexpress.email.vo.ShopMarketingCarListMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.email.service
 * @date:2020/4/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopMarketingCarListMailImplTest {

    @Autowired
    private TemplateMailService mailService;

    @Autowired
    private SendMailFactory sendMailFactory;

    @Test
    public void sendEmail() {

        int userId = 15937;
        String followCode = "followCode15937";
        String carUrl = "https://www.import-express.com/Goods/getShopCar";
        String emailFollowUrl = "https://www.import-express.com/followMe/index.do?fmc=" + followCode;

        ShopMarketingCarListMail.ShopMarketingCarListMailBuilder builder = ShopMarketingCarListMail.builder();
        builder.emailFollowUrl(emailFollowUrl).carUrl(carUrl).followCode(followCode).userId(String.valueOf(userId));

        String productCost = "110";
        String actualCost = "100";

        String totalProductCost = "110";

        String totalActualCost = "120";
        String offCost = "10";

        List<ShopCarMarketing> resultList = new ArrayList<>();
        List<ShopCarMarketing> sourceList = new ArrayList<>();

        builder.productCost(productCost)
                .actualCost(actualCost)
                .totalProductCost(totalProductCost)
                .totalActualCost(totalActualCost)
                .offCost(offCost)
                .updateList(JSONArray.toJSONString(resultList))
                .sourceList(JSONArray.toJSONString(sourceList));

        String userEmail = "1071083166@qq.com";
        String adminNameFirst = "FirstName";

        String adminName = "LastName";
        String adminEmail = "1071083166@qq.com";
        String whatsApp = "whatsApp";
        builder.userEmail(userEmail)
                .adminNameFirst(adminNameFirst)
                .adminName(adminName)
                .adminEmail(adminEmail)
                .whatsApp(whatsApp);

        MailBean mailBean = MailBean.builder().to(userEmail).bcc(adminEmail).siteEnum(SiteEnum.KIDS)
                    .subject("SHOPPING_CART_NO_CHANGE emailTitle").type(1)
                .templateType(TemplateType.SHOPPING_CART_NO_CHANGE).isTest(true).build();

        ShopMarketingCarListMail build = builder.build();
        build.setMailBean(mailBean);

        sendMailFactory.sendMail(mailService.processTemplate(build));
    }
}
