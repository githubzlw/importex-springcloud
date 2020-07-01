package com.importexpress.utils.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.utils.service.IpService;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 免费调用次数：每月10000次
 * http://api.ipstack.com/
 * luohao518@gmail.com 123456
 */
@Service
public class IpServiceIPStackImpl implements IpService {

//    {
//        "ip": "183.192.127.67",
//            "type": "ipv4",
//            "continent_code": "AS",
//            "continent_name": "Asia",
//            "country_code": "CN",
//            "country_name": "China",
//            "region_code": "SH",
//            "region_name": "Shanghai",
//            "city": "Shanghai",
//            "zip": "200000",
//            "latitude": 31.228469848632812,
//            "longitude": 121.47020721435547,
//            "location": {
//        "geoname_id": 1796236,
//                "capital": "Beijing",
//                "languages": [
//        {
//            "code": "zh",
//                "name": "Chinese",
//                "native": "中文"
//        }
//        ],
//        "country_flag": "http://assets.ipstack.com/flags/cn.svg",
//                "country_flag_emoji": "🇨🇳",
//                "country_flag_emoji_unicode": "U+1F1E8 U+1F1F3",
//                "calling_code": "86",
//                "is_eu": false
//    }
//    }
    private static final String URL = "http://api.ipstack.com/%s?access_key=1fb377f34291a1f6fa7d5e3fe0065709&&fields=country_code";

    public static void main(String[] args) throws Exception {
        System.out.println(new IpServiceIPStackImpl().queryIp("108.162.215.124"));
    }

    @Override
    public JSONObject queryIp(String ip) throws IOException {
        return UrlUtil.getInstance().callUrlByGet(String.format(URL, ip));
    }


}
