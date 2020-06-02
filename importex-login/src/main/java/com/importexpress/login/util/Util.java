package com.importexpress.login.util;

import com.importexpress.comm.pojo.SiteEnum;
import com.importexpress.login.bean.ConfigValuesBean;

/**
 * @Author jack.luo
 * @create 2020/5/29 11:45
 * Description
 */
public class Util {

    public static ConfigValuesBean getConfigValues(SiteEnum site, Config config) {

        String strGoogleClientId;
        String strFacebookClientId;
        String strFacebookClientSecret;
        switch (site){
            case IMPORTX:
                strGoogleClientId=config.IMPORT_GOOGLE_CLIENT_ID;
                strFacebookClientId=config.IMPORT_FACEBOOK_CLIENT_ID;
                strFacebookClientSecret=config.IMPORT_FACEBOOK_CLIENT_SECRET;
                break;
            case KIDS:
                strGoogleClientId=config.KIDS_GOOGLE_CLIENT_ID;
                strFacebookClientId=config.KIDS_FACEBOOK_CLIENT_ID;
                strFacebookClientSecret=config.KIDS_FACEBOOK_CLIENT_SECRET;
                break;
            case PETS:
                strGoogleClientId=config.PETS_GOOGLE_CLIENT_ID;
                strFacebookClientId=config.PETS_FACEBOOK_CLIENT_ID;
                strFacebookClientSecret=config.PETS_FACEBOOK_CLIENT_SECRET;
                break;
            default:
                throw new IllegalStateException();

        }
        return ConfigValuesBean.builder().googleClientId(strGoogleClientId)
                .facebookClientId(strFacebookClientId)
                .facebookClientSecret(strFacebookClientSecret).build();
    }
}
