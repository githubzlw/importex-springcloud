package com.importexpress.comm.pojo;

/**
 * @author jack.luo
 * @date 2019/6/25
 */
public enum SiteEnum {

    /**import express网站*/
    IMPORTX(1,"https://www.import-express.com","ImportExpress"),
    /**童装网站*/
    KIDS(2,"https://www.kidsproductwholesale.com","KidsProductWholesale"),
    /**宠物网站*/
    PETS(4,"https://www.petstoreinc.com","PetStoreInc"),
    /**家居网站*/
    HOME(8,"https://www.homeproductimport.com","HomeProductImport"),
    /**医疗网站*/
    MEDIC(16,"https://www.medicaldevicefactory.com","MedicalDeviceFactory"),
    /**管接头网站*/
    E_PIPE(32,"https://www.pipe.com","E_PIPE"),
    /**电缆网站*/
    LINE(64,"https://www.line.com","LINE"),
    /**HS_CODE网站*/
    S_HS_CODE(128,"https://www.HS_CODE.com","S_HS_CODE");

    private int code;

    private String url;

    /**
     * 网站名称
     */
    private String name;

    SiteEnum(int code){
        this.code = code;
    }

    SiteEnum(int code, String url){
        this.url = url;
        this.code = code;
    }

    SiteEnum(int code, String url,String name){
        this.url = url;
        this.code = code;
        this.name = name;
    }

    public int getCode(){
        return this.code;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args){
        System.out.println(SiteEnum.valueOf("importx"));
        System.out.println(SiteEnum.valueOf("pets"));
        System.out.println(SiteEnum.valueOf("pets").code);
    }
}
