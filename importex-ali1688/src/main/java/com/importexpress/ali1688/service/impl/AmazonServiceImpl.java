package com.importexpress.ali1688.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.model.ItemDetails;
import com.importexpress.ali1688.service.AmazonCacheService;
import com.importexpress.ali1688.service.AmazonService;
import com.importexpress.ali1688.util.Config;
import com.importexpress.ali1688.util.DataDealUtil;
import com.importexpress.ali1688.util.InvalidPid;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.exception.BizErrorCodeEnum;
import com.importexpress.comm.exception.BizException;
import com.importexpress.comm.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.ali1688.service.impl
 * @date:2020/5/6
 */
@Service
@Slf4j
public class AmazonServiceImpl implements AmazonService {

    private final StringRedisTemplate redisTemplate;
    private final AmazonCacheService cacheService;

    private static final String REDIS_CALL_COUNT = "amazon:call:count";
    private static final String REDIS_PID_COUNT = "amazon:pid:count";
    private static final String YYYYMMDD = "yyyyMMdd";
    private final Config config;

    private final static String URL_ITEM_DETAILS = "https://api.onebound.cn/amazon/api_call.php?api_name=item_get&lang=en&key=%s&secret=%s&num_iid=%s";

    public AmazonServiceImpl(StringRedisTemplate redisTemplate, AmazonCacheService cacheService, Config config) {
        this.redisTemplate = redisTemplate;
        this.cacheService = cacheService;
        this.config = config;
    }


    @Override
    public CommonResult getDetails(String pid) {
        JSONObject itemInfo = getItemInfo(pid, true);
        // 转换成bean
        if (null != itemInfo && itemInfo.containsKey("item")) {
            ItemDetails itemDetail = new ItemDetails();

            JSONObject itemJson = itemInfo.getJSONObject("item");
            itemDetail.setNum_iid(itemJson.getString("num_iid"));
            itemDetail.setTitle(itemJson.getString("title"));
            itemDetail.setPrice(DataDealUtil.dealAliPriceAndChange(itemJson.getString("price")));
            itemDetail.setOrginal_price(DataDealUtil.dealAliPriceAndChange(itemJson.getString("orginal_price")));
            itemDetail.setDetail_url(itemJson.getString("detail_url"));
            itemDetail.setPic_url(itemJson.getString("pic_url"));
            itemDetail.setBrand(itemJson.getString("brand"));
            itemDetail.setDesc(itemJson.getString("desc"));

            // 橱窗图
            List<String> item_imgs = new ArrayList<>();
            if (itemJson.containsKey("item_imgs")) {
                JSONArray item_imgArr = JSONArray.parseArray(itemJson.getString("item_imgs"));
                if (null != item_imgArr && item_imgArr.size() > 0) {
                    for (int i = 0; i < item_imgArr.size(); i++) {
                        item_imgs.add(item_imgArr.getJSONObject(i).getString("url"));
                    }
                }
            }
            itemDetail.setItem_imgs(item_imgs);

            // 规格数据
            Map<String, String> prop_imgMap = new HashMap<>();
            if (itemJson.containsKey("prop_imgs") && itemJson.getJSONObject("prop_imgs").containsKey("prop_img")) {
                JSONArray prop_imgArr = itemJson.getJSONObject("prop_imgs").getJSONArray("prop_img");
                if (null != prop_imgArr && prop_imgArr.size() > 0) {
                    for (int i = 0; i < prop_imgArr.size(); i++) {
                        prop_imgMap.put(prop_imgArr.getJSONObject(i).getString("properties"),
                                prop_imgArr.getJSONObject(i).getString("url"));
                    }
                    prop_imgArr.clear();
                }
            }

            List<JSONObject> skuList = new ArrayList<>();
            if (itemJson.containsKey("skus") && itemJson.getJSONObject("skus").containsKey("sku")) {
                JSONArray skuArr = itemJson.getJSONObject("skus").getJSONArray("sku");
                if (null != skuArr && skuArr.size() > 0) {
                    for (int i = 0; i < skuArr.size(); i++) {
                        JSONObject skuClJson = new JSONObject();
                        String price = skuArr.getJSONObject(i).getString("price");
                        String orginal_price = skuArr.getJSONObject(i).getString("orginal_price");
                        String properties = skuArr.getJSONObject(i).getString("properties");
                        String properties_name = skuArr.getJSONObject(i).getString("properties_name");
                        String quantity = skuArr.getJSONObject(i).getString("quantity");
                        String sku_id = skuArr.getJSONObject(i).getString("sku_id");
                        String img = "";
                        if (StringUtils.isNotBlank(properties)) {
                            String[] propList = properties.split(";");
                            for (String propCl : propList) {
                                if (prop_imgMap.containsKey(propCl)) {
                                    img = prop_imgMap.get(propCl);
                                    break;
                                }
                            }
                        }
                        skuClJson.put("price", DataDealUtil.dealAliPriceAndChange(price));
                        skuClJson.put("orginal_price", DataDealUtil.dealAliPriceAndChange(orginal_price));
                        skuClJson.put("properties", properties);
                        skuClJson.put("properties_name", properties_name);
                        skuClJson.put("quantity", quantity);
                        skuClJson.put("sku_id", sku_id);
                        skuClJson.put("img", img);
                        skuList.add(skuClJson);
                    }
                    skuArr.clear();
                }
            }
            itemDetail.setSku(skuList);

            // 规格标签展示
            JSONObject typeRsJson = new JSONObject();
            if (itemJson.containsKey("props_list")) {
                Map<String, Object> props_list = itemJson.getJSONObject("props_list").getInnerMap();

                props_list.forEach((k, v) -> {
                    JSONObject typeJson = new JSONObject();
                    typeJson.put("id", k);
                    String[] vlist = v.toString().split(":");
                    if (null != vlist && vlist.length == 2) {
                        typeJson.put("label", vlist[0]);

                        typeJson.put("val", vlist[1]);
                        if (prop_imgMap.containsKey(k)) {
                            typeJson.put("img", prop_imgMap.get(k));
                        } else {
                            typeJson.put("img", "");
                        }
                        if (typeRsJson.containsKey(vlist[0])) {
                            typeRsJson.getJSONArray(vlist[0]).add(typeJson);
                        } else {
                            JSONArray array = new JSONArray();
                            array.add(typeJson);
                            typeRsJson.put(vlist[0], array);
                        }
                    }
                });
            }
            itemDetail.setTypeJson(typeRsJson);


            // 解析属性标签
            List<Map> parseArray = JSONArray.parseArray(itemJson.getString("props"), Map.class);
            JSONObject propsMap = new JSONObject();
            if (null != parseArray && parseArray.size() > 0) {
                parseArray.forEach(e -> {
                    propsMap.put(e.get("name").toString(), e.get("value").toString());
                });
                parseArray.clear();
            }
            itemDetail.setProps(propsMap);


            return CommonResult.success(itemDetail);
        } else {
            return CommonResult.failed("no data");
        }
    }


    private JSONObject getItemInfo(String pid, boolean isCache) {
        Objects.requireNonNull(pid);
        if (isCache) {
            JSONObject itemFromRedis = this.cacheService.getItemInfo(pid);
            if (null != itemFromRedis) {
                checkPidInfo(pid, itemFromRedis);
                return itemFromRedis;
            }
        }

        Optional<JSONObject> jsonObjectOpt = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_DETAILS, config.API_KEY, config.API_SECRET, pid));
        if (jsonObjectOpt.isPresent()) {
            String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
            this.redisTemplate.opsForHash().increment(REDIS_PID_COUNT, "pid_" + strYmd, 1);
            JSONObject jsonObject = jsonObjectOpt.get();
            String error = jsonObject.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                if (error.contains("你的授权已经过期")) {
                    throw new BizException(BizErrorCodeEnum.EXPIRE_FAIL);
                } else if (error.contains("超过")) {
                    //TODO
                    throw new BizException(BizErrorCodeEnum.LIMIT_EXCEED_FAIL);
                } else if (error.contains("item-not-found")) {
                    throw new IllegalStateException("item-not-found");
                }
                log.warn("json's error is not empty:[{}]，pid:[{}]", error, pid);
                jsonObject = InvalidPid.of(Long.parseLong(pid), error);
            }

            checkPidInfo(pid, jsonObject);
            this.cacheService.setItemInfo(pid, jsonObject);

            return jsonObject;
        } else {
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }

    }


    private void checkPidInfo(String pid, JSONObject jsonObject) {
        Objects.requireNonNull(pid);
        Objects.requireNonNull(jsonObject);
        JSONObject item = jsonObject.getJSONObject("item");
        if (null == item || !item.containsKey("num_iid")) {
            // 保存2小时
            this.cacheService.setItemInfoTime(pid, jsonObject, 2);
            log.warn("itemInfos is null ,pid:[{}]", pid);
            throw new BizException(BizErrorCodeEnum.ITEM_IS_NULL);
        }
    }

}
