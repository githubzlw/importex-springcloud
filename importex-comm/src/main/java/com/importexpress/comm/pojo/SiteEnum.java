package com.importexpress.comm.pojo;

/**
 * @author jack.luo
 * @date 2019/6/25
 */
public enum SiteEnum {


    IMPORTX(1,"https://www.import-express.com"),KIDS(2,"https://www.kidsproductwholesale.com"),PETS(4,"https://www.petstoreinc.com"),HOME(8,"https://www.homeproductimport.com"),MEDIC(16,"https://www.medicaldevicefactory.com");

    private int code;

    private String url;

    SiteEnum(int code){
        this.code = code;
    }

    SiteEnum(int code, String url){
        this.url = url;
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }

    public String getUrl() {
        return url;
    }

    public static void main(String[] args){
        System.out.println(SiteEnum.valueOf("importx"));
        System.out.println(SiteEnum.valueOf("pets"));
        System.out.println(SiteEnum.valueOf("pets").code);
    }
}
