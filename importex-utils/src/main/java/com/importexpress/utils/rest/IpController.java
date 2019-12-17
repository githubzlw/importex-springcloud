package com.importexpress.utils.rest;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.utils.service.IpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author jack.luo
 */
@RestController
@Slf4j
@Api(tags = "IP查询接口")
public class IpController {

    public static final String REDIS_HASH_IP = "utils:ip";

    private IpService ipService;

    private StringRedisTemplate redisTemplate;

    @Autowired
    public IpController(IpService ipService, StringRedisTemplate redisTemplate) {
        this.ipService = ipService;
        this.redisTemplate = redisTemplate;
    }


    @GetMapping("/ip")
    @ApiOperation("IP查询")
    public CommonResult ip(@NonNull String ip) {

        //from cache read
        String result = (String) this.redisTemplate.opsForHash().get(REDIS_HASH_IP, ip);
        if (StringUtils.isNotEmpty(result)) {
            return CommonResult.success(result);
        } else {
            //from url read
            try {
                JSONObject json = this.ipService.queryIp(ip);
                if (!"success".equals(json.getString("status"))) {
                    return CommonResult.failed("ip search result is fault");
                } else {
                    String country = json.getString("countryCode");
                    this.redisTemplate.opsForHash().put(REDIS_HASH_IP, ip, country);
                    return CommonResult.success(country);
                }
            } catch (IOException e) {
                return CommonResult.failed(e.getMessage());
            }
        }

    }

}
