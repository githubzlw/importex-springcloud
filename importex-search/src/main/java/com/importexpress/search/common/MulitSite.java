package com.importexpress.search.common;

public class MulitSite {

    public static SiteEnum siteEnum;

    public static SiteEnum getSiteEnum(int site) {
        switch (site){
            case 1:
                return SiteEnum.IMPORT;
            case 2:
                return SiteEnum.KIDS;
            case 4:
                return SiteEnum.PETS;
        }
        return SiteEnum.IMPORT;
    }
}
