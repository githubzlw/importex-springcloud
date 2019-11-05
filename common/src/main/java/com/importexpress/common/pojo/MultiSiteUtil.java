package com.importexpress.common.pojo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author luohao
 * @date 2019/6/25
 */
@Slf4j
public class MultiSiteUtil {

    public static SiteEnum siteEnum;

    public static int site;

    static {
        siteEnum=SiteEnum.KIDS;
        site = siteEnum.getCode();
    }



    public static char getSiteType(){

        switch (siteEnum){
            case IMPORTX:
                return 'I';
            case KIDS:
                return 'K';
            case PETS:
                return 'P';
            default:
                log.error("Unsupport siteEnum:[{}]",siteEnum);
                throw new IllegalArgumentException("Unsupport siteEnum:"+siteEnum);
        }
    }

    public static boolean checkBit(int val, int pos){
        return ((val>>(pos-1))&1)==1?true:false;
    }

    public static void main(String[] args){
        System.out.println(checkBit(1, 1));
        System.out.println(checkBit(2, 2));
        System.out.println(checkBit(2, 1));
        System.out.println(checkBit(4, 3));
        System.out.println(checkBit(4, 2));


    }
}
