package com.importexpress.ali1688.util;

import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author luohao
 * @date 2019/11/13
 */
public class InvalidPid {

    private InvalidPid(){

    }

    public static JSONObject of(Long pid,String resason){
        JSONObject jsonObject = new JSONObject();
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        jsonObject.put("secache_date", now);
        jsonObject.put("server_time", now);
        jsonObject.put("reason", resason);
        jsonObject.put("pid", pid);
        return jsonObject;
    }
}
