package com.importexpress.utils.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

/**
 * @author jack.luo
 * @date 2019/11/18
 */
public interface IpService {

    JSONObject queryIp(String ip) throws IOException;
}
