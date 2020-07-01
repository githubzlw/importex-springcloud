package com.importexpress.login.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.login.service.ThirdPartyLoginService;
import com.importexpress.login.util.Config;
import com.importexpress.login.bean.ConfigValuesBean;
import com.importexpress.login.bean.FacebookPojo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;

import static com.importexpress.login.util.Util.getConfigValues;


/**
 * 串口通信
 *
 * @Author jack.luo
 * @create 2020/05/18
 * Description
 */
@Service
@Slf4j
public class ThirdPartyLoginServiceImpl implements ThirdPartyLoginService {


    private final static String FACEBOOK_LOGIN_URL = "https://www.facebook.com/dialog/oauth?client_id=%s&redirect_uri=%s/user/facebookLogin&scope=email,public_profile&fields=name,email";

    private final static String FACEBOOK_ME_URL = "https://graph.facebook.com/oauth/access_token?redirect_uri=%s/user/facebookLogin&client_id=%s&client_secret=%s&code=%s";

    private final static String FACEBOOK_TOKEN_URL = "https://graph.facebook.com/me?fields=id,name,email&access_token=%s";

    private final Config config;

    public ThirdPartyLoginServiceImpl(Config config) {
        this.config = config;
    }

    /**
     * google login auth
     * @param site
     * @param idTokenString
     * @return
     * @throws IOException
     */
    @Override
    public ImmutablePair<String, String> googleAuth(SiteEnum site, String idTokenString) throws IOException {

        ConfigValuesBean configValues = getConfigValues(site, config);
        try {

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(configValues.getGoogleClientId())).build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleUserId = payload.getSubject();
            String googleEmail = payload.getEmail();

            return new ImmutablePair<>(googleUserId, googleEmail);

        } catch (GeneralSecurityException | IOException e) {
            throw new IOException("googleAuth.GeneralSecurityException");
        }

    }

    /**
     * get GoogleClientId
     * @param site
     * @return
     */
    @Override
    public String getGoogleClientId(SiteEnum site) {

        ConfigValuesBean configValues = getConfigValues(site, config);


        return configValues.getGoogleClientId();
    }

    /**
     * get facebook login url
     * @param site
     * @return
     */
    @Override
    public String getFacebookUrl(SiteEnum site) {

        ConfigValuesBean configValues = getConfigValues(site, config);


        return String.format(FACEBOOK_LOGIN_URL
                , configValues.getFacebookClientId(), site.getUrl());
    }

    /**
     * facebook login auth
     * @param site
     * @param code
     * @return
     */
    @Override
    public FacebookPojo facebookAuth(SiteEnum site, String code) {

        if(StringUtils.isEmpty(code)){
            throw new IllegalArgumentException("code is empty");
        }

        ConfigValuesBean configValues = getConfigValues(site, config);


        String accessTokenUrl = String.format(FACEBOOK_ME_URL
                , site.getUrl(), configValues.getFacebookClientId(), configValues.getFacebookClientSecret(), code);
        log.info("accessTokenURL:[{}]", accessTokenUrl);
        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, String> result = restTemplate.getForObject(accessTokenUrl, HashMap.class);
        assert result != null;
        String accessToken = result.get("access_token");
        log.info("get access token:[{}] success", accessToken);

        return restTemplate.getForObject(String.format(FACEBOOK_TOKEN_URL, accessToken), FacebookPojo.class);

    }


}
