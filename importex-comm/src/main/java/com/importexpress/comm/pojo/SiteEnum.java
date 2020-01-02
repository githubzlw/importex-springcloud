package com.importexpress.comm.pojo;

/**
 * @author jack.luo
 * @date 2019/6/25
 */
public enum SiteEnum {


    IMPORTX(1,"https://www.import-express.com","ImportExpress"),
    KIDS(2,"https://www.kidsproductwholesale.com","KidsProductWholesale"),
    PETS(4,"https://www.petstoreinc.com","PetStoreInc"),
    HOME(8,"https://www.homeproductimport.com","HomeProductImport"),
    MEDIC(16,"https://www.medicaldevicefactory.com","MedicalDeviceFactory");

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
