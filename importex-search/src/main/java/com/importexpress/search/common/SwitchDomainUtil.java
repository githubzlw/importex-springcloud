package com.importexpress.search.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 域名切换
 */
@Component
public class SwitchDomainUtil {
    private static final String IMPORT_WEBSITE = "import-express.com";
    private static final String KID_WEBSITE = "kidsproductwholesale.com";
    private static final String PET_WEBSITE = "petstoreinc.com";
    private static final String HOME_WEBSITE = "homeproductimport.com";
    private static final String MEDIC_WEBSITE = "medicaldevicefactory.com";
//    public static final String HTTP_IMPORT_WEBSITE = "https://www.import-express.com";
//    public static final String HTTP_KID_WEBSITE = "https://www.kidsproductwholesale.com";
//    public static final String HTTP_PET_WEBSITE = "https://www.petstoreinc.com";
//
//    private static final String IMPORT_WEBSITE_VIDEOURL_1 = "img1.import-express.com";
//    private static final String IMPORT_WEBSITE_VIDEOURL = "img.import-express.com";
//    private static final String KID_WEBSITE_VIDEOURL = "img1.kidsproductwholesale.com";
//    private static final String PET_WEBSITE_VIDEOURL = "img1.petstoreinc.com";

    /**
     * 检查是否未null和替换
     *
     * @param oldStr
     * @return
     */
    public static String checkIsNullAndReplace(String oldStr,int site) {
        if (StringUtils.isBlank(oldStr)) {
            return oldStr;
        }
        String tempStr;
        switch (site) {
            case 2:
                tempStr = oldStr.replace(IMPORT_WEBSITE, KID_WEBSITE);
                break;
            case 4:
                tempStr = oldStr.replace(IMPORT_WEBSITE, PET_WEBSITE);
                break;
            case 8:
                tempStr = oldStr.replace(IMPORT_WEBSITE, HOME_WEBSITE);
                break;
            case 16:
                tempStr = oldStr.replace(IMPORT_WEBSITE, MEDIC_WEBSITE);
                break;
            default:
                tempStr = oldStr;
        }
        return tempStr;
    }
    /**
     * 对应网站提示词结果处理
     *
     * @param keyword
     * @return
     */
    public static String correctAutoResult(String keyword,int site) {
        switch (site) {
            case 2:
                keyword = keyword.substring(5);
                break;
            case 4:
                keyword = keyword.substring(4);
                break;
            default:
                keyword = keyword.replace("import-","");
        }
        keyword = keyword.replaceAll("(\\-+)"," ");
        return keyword;
    }
    /**
     * 对应网站提示词处理
     *
     * @param keyword
     * @return
     */
    public static String switchAutoKey(String keyword,int site) {
        if(StringUtils.isBlank(keyword)){
            return keyword;
        }
        keyword = keyword.replaceAll("(\\s+)","-");
        switch (site) {
            case 2:
                keyword = "kids-"+keyword;
                break;
            case 4:
                keyword = "pets-"+keyword;
                break;
            default:
                keyword = "import-"+keyword;
        }
        return keyword;
    }

}
