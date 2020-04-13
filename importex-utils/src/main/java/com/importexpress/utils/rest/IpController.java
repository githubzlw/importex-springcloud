package com.importexpress.utils.rest;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.utils.service.IpService;
import com.importexpress.utils.util.GeoIpUtils;
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
            try {
                //本地数据库搜索ip
                String countryCode = GeoIpUtils.getInstance().getCountryCode(ip);

                if(StringUtils.isEmpty(countryCode)) {
                    //线上搜索ip
                    JSONObject json = this.ipService.queryIp(ip);
                    countryCode = json.getString("country_code");
                    if (StringUtils.isEmpty(countryCode)) {
                      throw new IOException("ip lookup failed");
                    }
                }
                this.redisTemplate.opsForHash().put(REDIS_HASH_IP, ip, countryCode);
                return CommonResult.success(countryCode);
            } catch (Exception e) {
                return CommonResult.failed(e.getMessage());
            }
        }
    }

    @GetMapping("/clearcache")
    @ApiOperation("clearCache")
    public CommonResult clearCache() {

        Boolean isOK = this.redisTemplate.delete(REDIS_HASH_IP);
        if(isOK!=null && isOK){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

}
