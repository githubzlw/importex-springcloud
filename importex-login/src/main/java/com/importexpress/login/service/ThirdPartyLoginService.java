package com.importexpress.login.service;


import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.login.bean.FacebookPojo;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;

/**
 * @author jack.luo
 * @date 2020/05/18
 */
public interface ThirdPartyLoginService {

    /**
     * google login auth
     * @param site
     * @param idTokenString
     * @return
     * @throws IOException
     */
    ImmutablePair<String, String> googleAuth(SiteEnum site, String idTokenString) throws IOException;


    /**
     * get GoogleClientId
     * @param site
     * @return
     */
    String getGoogleClientId(SiteEnum site);

    /**
     * get facebook login url
     * @param site
     * @return
     */
    String getFacebookUrl(SiteEnum site);

    /**
     * facebook login auth
     * @param site
     * @param code
     * @return
     */
    FacebookPojo facebookAuth(SiteEnum site, String code);
}
