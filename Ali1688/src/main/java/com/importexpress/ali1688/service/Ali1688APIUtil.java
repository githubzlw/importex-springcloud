package com.importexpress.ali1688.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author luohao
 * @date 2019/11/4
 */
@Slf4j
public class Ali1688APIUtil {

    /**
     * 获取商品详情
     */
    private static final String URL = "http://api.onebound.cn/1688/api_call.php?num_iid=%s&cache=no&api_name=item_get&lang=en&key=tel13661551626&secret=20191104";

    /**
     * singleton
     */
    private static Ali1688APIUtil singleton = null;

    /**
     * The singleton HTTP client.
     */
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    /**
     * 构造函数
     */
    private Ali1688APIUtil() {

    }

    /**
     * getInstance
     * @return
     */
    public static Ali1688APIUtil getInstance() {

        if (singleton == null) {
            synchronized (Ali1688APIUtil.class) {
                if (singleton == null) {
                    singleton = new Ali1688APIUtil();
                }
            }
        }
        return singleton;
    }

    /**
     * 1688商品详情查询
     * @param pid
     * @return
     */
    public JSONObject getItem(Long pid) {

        try {
            JSONObject jsonObject = callURLByGet(String.format(URL, pid));
            String error = jsonObject.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                log.warn("The pid:[{}] is not invalid.", pid);
                return null;
            } else {
                return jsonObject;
            }
        } catch (IOException e) {
            log.error("getItem", e);
            return null;
        }
    }

    /**
     * get items by pid array
     * @param pids
     * @return
     */
    public List<JSONObject> getItems(Long[] pids) {

        List<JSONObject> lstResult = new ArrayList<>(pids.length);

        List<Long> lstPids = Arrays.asList(pids);
        lstPids.stream().parallel().forEach( pid -> {
            JSONObject item = Ali1688APIUtil.getInstance().getItem(pid);
            if(item!=null){
                lstResult.add(item);
            }else{
                lstResult.add(getNotExistPid());
            }
        });
        return lstResult;
    }


    /**
     * return offline pid json object
     * @return
     */
    private JSONObject getNotExistPid() {
        JSONObject jsonObject = new JSONObject();
        LocalDateTime now = LocalDateTime.now();
        jsonObject.put("secache_date", now);
        jsonObject.put("server_time", now);
        jsonObject.put("item", null);
        return jsonObject;
    }

    /**
     * 调用URL（Get）
     * @param URL
     * @return
     * @throws IOException
     */
    private JSONObject callURLByGet(String URL) throws IOException {

        Request request = new Request.Builder().url(URL).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("response is not successful");
        }
        return JSON.parseObject(response.body().string());
    }

}
