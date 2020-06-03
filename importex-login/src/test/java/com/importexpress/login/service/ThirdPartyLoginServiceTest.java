package com.importexpress.login.service;

import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.login.util.Config;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author jack.luo
 * @create 2020/5/29 13:22
 * Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThirdPartyLoginServiceTest{

    @Autowired
    private ThirdPartyLoginService service;

    private final static String FACEBOOK_LOGIN_URL = "https://www.facebook.com/dialog/oauth?client_id=%s&redirect_uri=%s/user/facebookLogin&scope=email,public_profile&fields=name,email";

    private final static String FACEBOOK_ME_URL = "https://graph.facebook.com/oauth/access_token?redirect_uri=%s/user/facebookLogin&client_id=%s&client_secret=%s&code=%s";

    private final static String FACEBOOK_TOKEN_URL = "https://graph.facebook.com/me?fields=id,name,email&access_token=%s";

    @Autowired
    private Config config;


    @Test
    public void testGetGoogleClientId() {
        Assert.assertEquals(config.IMPORT_GOOGLE_CLIENT_ID,service.getGoogleClientId(SiteEnum.IMPORTX));
        Assert.assertEquals(config.KIDS_GOOGLE_CLIENT_ID,service.getGoogleClientId(SiteEnum.KIDS));
        Assert.assertEquals(config.PETS_GOOGLE_CLIENT_ID,service.getGoogleClientId(SiteEnum.PETS));
    }

    @Test
    public void testGetFacebookUrl() {

        String url = String.format(FACEBOOK_LOGIN_URL, config.IMPORT_FACEBOOK_CLIENT_ID, SiteEnum.IMPORTX.getUrl());
        Assert.assertEquals(url,service.getFacebookUrl(SiteEnum.IMPORTX));

        url = String.format(FACEBOOK_LOGIN_URL, config.KIDS_FACEBOOK_CLIENT_ID, SiteEnum.KIDS.getUrl());
        Assert.assertEquals(url,service.getFacebookUrl(SiteEnum.KIDS));

        url = String.format(FACEBOOK_LOGIN_URL, config.PETS_FACEBOOK_CLIENT_ID, SiteEnum.PETS.getUrl());
        Assert.assertEquals(url,service.getFacebookUrl(SiteEnum.PETS));
    }


}