package com.importexpress.utils.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author luohao
 * @date 2019/11/18
 */
public interface IpService {

    JSONObject queryIp(String ip) throws IOException;
}
