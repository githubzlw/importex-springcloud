package com.importexpress.utils.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import com.importexpress.utils.service.IpService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IpServiceTaobaoImpl implements IpService {

//    {"code":0,"data":{"ip":"108.162.215.124","country":"美国","area":"","region":"加利福尼亚","city":"XX","county":"XX","isp":"XX","country_id":"US","area_id":"","region_id":"US_104","city_id":"xx","county_id":"xx","isp_id":"xx"}}

    private static final String URL = "http://ip.taobao.com/service/getIpInfo2.php?ip=%s";

    public static void main(String[] args) throws Exception {
        System.out.println(new IpServiceTaobaoImpl().queryIp("108.162.215.124"));
    }

    @Override
    public JSONObject queryIp(String ip) throws IOException {
        return UrlUtil.getInstance().callUrlByGet(String.format(URL, ip));
    }


}
