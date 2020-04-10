package com.importexpress.utils.util;

import java.io.File;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

/**
 * @Description: geoip工具类
 */
public class GeoIpUtils {

    private final static Logger logger = LoggerFactory.getLogger(GeoIpUtils.class);

    private static DatabaseReader reader;

    private static DatabaseReader getReader(){
        try{
            if(reader == null){
                logger.warn("打开ip数据库");
                File database =  new File(GeoIpUtils.class.getClassLoader().getResource("GeoLite2-Country.mmdb").getFile()); // 附件下载百度云地址https://pan.baidu.com/s/1ENqTeCoMIWJMbh88nYU5gg
                reader = new DatabaseReader.Builder(database).build();
            }
            return reader;
        }catch(Exception e){
            e.printStackTrace();
            return reader;
        }

    }

    /**
     * 根据ip获取国家对象,不存在则返回null
     * @param ip
     * @return
     */
    public static Country getCountry(String ip){
        try{
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = getReader().country(ipAddress);
            Country country = response.getCountry();
            return country;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 根据ip获取国家代码,不存在则返回null
     * @param ip
     * @return
     */
    public static String getCountryCode(String ip){
        Country country = getCountry(ip);
        return country != null ? country.getIsoCode() : null;
    }

    /**
     * 根据ip获取国家名称,不存在则返回null
     * @param ip
     * @return
     */
    public static String getCountryName(String ip){
        Country country = getCountry(ip);
        return country != null ? country.getName() : null;
    }

    public static void main(String[] args){

        System.out.println(GeoIpUtils.getCountryName("108.162.215.124")); // China
        System.out.println(GeoIpUtils.getCountryCode("108.162.215.124")); // CN
    }
}