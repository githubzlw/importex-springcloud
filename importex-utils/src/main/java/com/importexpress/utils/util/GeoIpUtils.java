package com.importexpress.utils.util;

import java.io.InputStream;
import java.net.InetAddress;

import lombok.extern.slf4j.Slf4j;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author jack.luo
 * @Description: geoip工具类
 */
@Slf4j
public class GeoIpUtils {

    private GeoIpUtils(){

    }

    private enum Singleton{
        INSTANCE;

        private final GeoIpUtils instance;

        Singleton(){
            instance = new GeoIpUtils();
        }

        private GeoIpUtils getInstance(){
            return instance;
        }
    }

    public static GeoIpUtils getInstance(){

        return Singleton.INSTANCE.getInstance();
    }


    private DatabaseReader reader;

    private DatabaseReader getReader(){
        try{
            if(reader == null){
                log.info("打开ip数据库");
                Resource resource = new ClassPathResource("GeoLite2-Country.mmdb");
                try(InputStream inputStream=resource.getInputStream()){
                    reader = new DatabaseReader.Builder(inputStream).build();
                }
            }
            return reader;
        }catch(Exception e){
            log.error("getReader",e);
            return reader;
        }

    }

    /**
     * 根据ip获取国家对象,不存在则返回null
     * @param ip
     * @return
     */
    public Country getCountry(String ip){
        try{
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = getReader().country(ipAddress);
            return response.getCountry();
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 根据ip获取国家代码,不存在则返回null
     * @param ip
     * @return
     */
    public String getCountryCode(String ip){
        Country country = getCountry(ip);
        return country != null ? country.getIsoCode() : null;
    }

    /**
     * 根据ip获取国家名称,不存在则返回null
     * @param ip
     * @return
     */
    public String getCountryName(String ip){
        Country country = getCountry(ip);
        return country != null ? country.getName() : null;
    }

    public static void main(String[] args){

        System.out.println(GeoIpUtils.getInstance().getCountryName("108.162.215.124")); // China
        System.out.println(GeoIpUtils.getInstance().getCountryCode("108.162.215.124")); // CN
        System.out.println(GeoIpUtils.getInstance().getCountryCode("0.0.0.0"));
        System.out.println(GeoIpUtils.getInstance().getCountryCode("127.0.0.1"));
        System.out.println(GeoIpUtils.getInstance().getCountryCode("192.168.1.71"));

    }
}