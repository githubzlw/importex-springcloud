package com.importexpress.utils.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.utils.service.IpService;
import org.springframework.stereotype.Service;

import java.io.IOException;

//@Service
public class IpServiceImpl implements IpService {

    /**
     * {
     * "code": 0,
     * "data": {
     * "ip": "124.78.168.185",
     * "country": "中国",
     * "area": "",
     * "region": "上海",
     * "city": "上海",
     * "county": "XX",
     * "isp": "电信",
     * "country_id": "CN",
     * "area_id": "",
     * "region_id": "310000",
     * "city_id": "310100",
     * "county_id": "xx",
     * "isp_id": "100017"
     * }
     * }
     */

    private static final String URL = "http://ip-api.com/json/%s";

    public static void main(String[] args) throws Exception {
        System.out.println(new IpServiceImpl().queryIp("27.115.38.42"));
    }

    @Override
    public JSONObject queryIp(String ip) throws IOException {
        return UrlUtil.getInstance().callUrlByGet(String.format(URL, ip));
    }


}
