package com.importexpress.ali1688.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.importexpress.ali1688.mapper.PidQueueMapper;
import com.importexpress.ali1688.model.ItemDetails;
import com.importexpress.ali1688.model.PidQueue;
import com.importexpress.ali1688.service.Ali1688CacheService;
import com.importexpress.ali1688.service.Ali1688Service;
import com.importexpress.ali1688.util.Config;
import com.importexpress.ali1688.util.DataDealUtil;
import com.importexpress.ali1688.util.InvalidPid;
import com.importexpress.comm.domain.CommonResult;
import com.importexpress.comm.exception.BizErrorCodeEnum;
import com.importexpress.comm.exception.BizException;
import com.importexpress.comm.pojo.Ali1688Item;
import com.importexpress.comm.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author jack.luo
 * @date 2019/11/6
 */
@Slf4j
@Service
@Configuration
public class Ali1688ServiceImpl implements Ali1688Service {

    private static final int MAX_GOODS_NUMBER = 200;

    private static final String REDIS_CALL_COUNT = "ali:call:count";

    private static final String YYYYMMDD = "yyyyMMdd";

    private final StringRedisTemplate redisTemplate;

    /**
     * 获取商品详情（1688）
     */
    private final static String URL_ITEM_GET = "%s1688/api_call.php?key=%s&secret=%s&num_iid=%s&api_name=item_get&lang=zh-CN";

    /**
     * 获取商品详情（Alibaba）
     */
    private final static String URL_ITEM_GET_ALIBABA = "%salibaba/item_get/?key=%s&secret=%s&num_iid=%s&api_name=item_get&lang=zh-CN";

    private final static String URL_ITEM_GET_ALIEXPRESS = "%saliexpress/item_get/?key=%s&secret=%s&num_iid=%s&api_name=item_get&lang=zh-CN";

    /**
     * img_upload API URL
     */
    private final static String IMG_UPLOAD_TAOBAO_API = "%staobao/demo/img_upload.php";


    /**
     * img_upload API URL
     */
    private final static String IMG_UPLOAD_TAOBAO_API_1 = "%staobao/api_call.php";

    /**
     * image search API URL(图片搜索)
     */
    private final static String IMG_SEARCH_TAOBAO_API = "%staobao/item_search_img?imgid=%s&lang=en&key=%s&secret=%s";


    private final static String URL_TAOBAO_ITEM_DETAILS = "https://api.onebound.cn/taobao/api_call.php?is_promotion=1&api_name=item_get&lang=en&key=%s&secret=%s&num_iid=%s";
    private static final String REDIS_TAOBAO_PID_COUNT = "taobao:pid:count";

    /**
     * 获取店铺商品
     */
    private final static String URL_ITEM_SEARCH = "%s1688/api_call.php?key=%s&secret=%s&seller_nick=%s&start_price=0&end_price=0&q=&page=%d&cid=&api_name=item_search_shop&lang=zh-CN";
    private Ali1688CacheService ali1688CacheService;
    private Config config;
    private PidQueueMapper pidQueueMapper;

    @Autowired
    public Ali1688ServiceImpl(Ali1688CacheService ali1688CacheService, Config config, PidQueueMapper pidQueueMapper, StringRedisTemplate redisTemplate) {
        this.ali1688CacheService = ali1688CacheService;
        this.config = config;
        this.pidQueueMapper = pidQueueMapper;
        this.redisTemplate = redisTemplate;
    }


    /**
     * 通过PID搜索商品（1688）详情
     *
     * @param pid
     * @param isCache
     * @return
     */
    private JSONObject getItemByPid(Long pid, boolean isCache) {
        Objects.requireNonNull(pid);
        if (isCache) {
            JSONObject itemFromRedis = this.ali1688CacheService.getItem(pid);
            if (itemFromRedis != null) {
                checkItem(pid, itemFromRedis);
                return itemFromRedis;
            }
        }

        Optional<JSONObject> jsonObjectOpt = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_GET, config.API_HOST, config.API_KEY, config.API_SECRET, pid));
        if (jsonObjectOpt.isPresent()) {
            JSONObject jsonObject = jsonObjectOpt.get();
            String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
            this.redisTemplate.opsForHash().increment(REDIS_CALL_COUNT, "pid_" + strYmd, 1);
            String error = jsonObject.getString("error");
            //if(1==1) throw new IllegalStateException("testtesttest");
            if (StringUtils.isNotEmpty(error)) {
                if (error.contains("你的授权已经过期")) {
                    throw new BizException(BizErrorCodeEnum.EXPIRE_FAIL);
                } else if (error.contains("超过")) {
                    throw new BizException(BizErrorCodeEnum.LIMIT_EXCEED_FAIL);
                } else if (error.contains("item-not-found")) {
                    throw new IllegalStateException("item-not-found");
                }
                log.warn("json's error is not empty:[{}]，pid:[{}]", error, pid);
                jsonObject = InvalidPid.of(pid, error);
            }
            this.ali1688CacheService.saveItemIntoRedis(pid, jsonObject);
            checkItem(pid, jsonObject);

            return jsonObject;
        } else {
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }

    }

    /**
     * 1688商品详情查询（单个）
     *
     * @param pid
     * @return
     */
    @Override
    public JSONObject getItem(Long pid, boolean isCache) {
        Objects.requireNonNull(pid);

        Callable<JSONObject> callable = new Callable<JSONObject>() {

            @Override
            public JSONObject call() {
                return getItemByPid(pid, isCache);

            }
        };

        Retryer<JSONObject> retryer = RetryerBuilder.<JSONObject>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(2000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            return retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 1688商品详情查询（多个）
     *
     * @param pids
     * @return
     */
    @Override
    public List<JSONObject> getItems(Long[] pids, boolean isCache) {
        Objects.requireNonNull(pids);
        List<JSONObject> lstResult = new CopyOnWriteArrayList<JSONObject>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (long pid : pids) {
            executorService.execute(() -> {
                try {
                    JSONObject item = getItem(pid, isCache);
                    if (item != null) {
                        lstResult.add(item);
                    } else {
                        lstResult.add(InvalidPid.of(pid, "no data"));
                    }
                } catch (BizException be) {
                    if (be.getErrorCode() == BizErrorCodeEnum.DESC_IS_NULL) {
                        lstResult.add(InvalidPid.of(pid, BizErrorCodeEnum.DESC_IS_NULL.getDescription()));
                    }
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        }

        return lstResult;
    }

    /**
     * 获得店铺商品
     *
     * @param shopid
     * @return
     */
    @Override
    public List<Ali1688Item> getItemsInShop(String shopid) {
        Objects.requireNonNull(shopid);
        List<Ali1688Item> itemFromRedis = this.ali1688CacheService.getShop(shopid);
        if (itemFromRedis != null) {
            return itemFromRedis;
        }

        try {
            List<Ali1688Item> result = new ArrayList<>(100);

            Map<String, Integer> mapSum = fillItems(result, shopid, 1);
            if (mapSum == null) {
                //无数据
                return result;
            }
            int count = mapSum.get("pagecount") < MAX_GOODS_NUMBER ? mapSum.get("pagecount") : MAX_GOODS_NUMBER;
            int total = mapSum.get("total_results");

            if (count > 1) {
                for (int i = 2; i <= count; i++) {
                    fillItems(result, shopid, i);
                }
            }

//            if(result.size() != total){
//                log.warn("filled items's size is not same,need retry! : [{}]:[{}]",result.size(),total);
//                throw new IllegalStateException("filled items's size is not same,need retry!");
//            }

            //过滤掉销量=0的商品
            List<Ali1688Item> haveSaleItems = result.stream().filter(item -> item.getSalesOfParse() >= config.minSales).sorted(Comparator.comparing(Ali1688Item::getSalesOfParse, Comparator.reverseOrder())).collect(Collectors.toList());
            this.ali1688CacheService.setShop(shopid, haveSaleItems);
            return haveSaleItems;

        } catch (IOException e) {
            log.error("getItemsInShop", e);
            throw new IllegalStateException("io exception,need retry!");
        }
    }

    /**
     * 从缓存中获取图片搜索结果
     * @param md5Hex
     * @return
     */
    @Override
    public JSONObject getImageSearchFromCatch(String md5Hex){
        return ali1688CacheService.getImageSearch(md5Hex);
    }

    /**
     * 图片搜索结果保存到缓存中
     * @param md5
     * @param jsonObject
     * @return
     */
    @Override
    public int saveImageSearchFromCatch(String md5,JSONObject jsonObject){
        ali1688CacheService.saveImageSearch(md5,jsonObject);
        return 1;
    }

    /**
     * 上传图片到taobao
     *
     * @param file
     * @return
     */
    @Override
    public String uploadImgToTaobao(String file) throws IOException {

        String url=null;

        try {
            JSONObject jsonObject = UrlUtil.getInstance().doPostForImgUpload(String.format(IMG_UPLOAD_TAOBAO_API_1, config.API_HOST), "taobao", file,config.API_KEY, config.API_SECRET);
            if (jsonObject != null) {
                //sample:  tfsid -> https://img.alicdn.com/imgextra/i4/2601011849/O1CN01Ob6weI1PWsusJC7Xt_!!2601011849.jpg
                log.info("result:[{}]", jsonObject);
                url = jsonObject.getJSONObject("items").getJSONObject("item").getString("name");
                // url = jsonObject.getString("tfsid");
            }
        }catch(IllegalStateException ise) {
            log.warn("file size is error", ise);
            return null;
        }

        return url;
    }

    /**
     * 图片搜索
     * @param imgUrl
     * @return
     * @throws IOException
     */
    @Override
    public JSONObject searchImgFromTaobao(String imgUrl) throws IOException {

        Optional<JSONObject> jsonObjectOpt = UrlUtil.getInstance().callUrlByGet(String.format(IMG_SEARCH_TAOBAO_API, config.API_HOST, imgUrl, config.API_KEY, config.API_SECRET));
        if (jsonObjectOpt.isPresent()) {
            return jsonObjectOpt.get();
        } else {
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }
    }

    /**
     * 清除redis缓存里面下架商品
     * @return
     */
    @Override
    public int clearNotExistItemInCache() {
        return this.ali1688CacheService.processNotExistItemInCache(true);
    }

    /**
     * 清除redis缓存里面所有商品
     * @return
     */
    @Override
    public int clearAllPidInCache() {
        return this.ali1688CacheService.clearAllPidInCache();
    }

    /**
     * 清除redis缓存里面所有店铺
     * @return
     */
    @Override
    public int clearAllShopInCache() {
        return this.ali1688CacheService.clearAllShopInCache();
    }

    /**
     * 下架商品数量统计
     * @return
     */
    @Override
    public int getNotExistItemInCache() {
        return this.ali1688CacheService.processNotExistItemInCache(false);
    }

    /**
     * 设置key的过期时间
     * @param days
     */
    @Override
    public void setItemsExpire(int days) {
        this.ali1688CacheService.setItemsExpire(days);
    }

    /**
     * pid_queue表：获得pid（分页）
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public List<PidQueue> getAllPids(int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        Example example = new Example(PidQueue.class);
        example.setOrderByClause("update_time DESC");
        return this.pidQueueMapper.selectByExampleAndRowBounds(example, new RowBounds(offset, pageSize));
    }

    /**
     * pid_queue表：获得所有pid
     * @return
     */
    @Override
    public List<PidQueue> getAllPids() {

        Example example = new Example(PidQueue.class);
        example.setOrderByClause("update_time DESC");
        return this.pidQueueMapper.selectByExample(example);
    }


    /**
     * pid_queue表：获得UnStartpid
     * @return
     */
    @Override
    public List<PidQueue> getAllUnStartPids() {

        PidQueue pidQueue = new PidQueue();
        pidQueue.setStatus((byte) 0);
        return this.pidQueueMapper.select(pidQueue);
    }

    /**
     * pid_queue表：更新状态
     * @param id
     * @param status
     * @return
     */
    @Override
    public int updatePidQueue(int id, int status) {

        PidQueue pidQueue = new PidQueue();
        pidQueue.setId(id);
        pidQueue.setStatus((byte) status);
        pidQueue.setUpdateTime(new Date());
        return this.pidQueueMapper.updateByPrimaryKeySelective(pidQueue);
    }

    /**
     * pid_queue表：增加pid
     * @param shopId
     * @param pid
     * @return
     */
    @Override
    public int pushPid(String shopId, int pid) {
        PidQueue pidQueue = new PidQueue();
        pidQueue.setPid(pid);

        PidQueue select = this.pidQueueMapper.selectOne(pidQueue);
        if (select != null) {
            //update
            select.setShopId(shopId);
            select.setUpdateTime(new Date());
            return this.pidQueueMapper.updateByPrimaryKey(select);
        } else {
            //insert
            pidQueue.setShopId(shopId);
            pidQueue.setStatus((byte) 0);
            Date now = new Date();
            pidQueue.setCreateTime(now);
            pidQueue.setUpdateTime(now);
            return this.pidQueueMapper.insert(pidQueue);
        }
    }

    /**
     * pid_queue表：删除pid
     * @param id
     * @return
     */
    @Override
    public int deleteIdInQueue(int id) {

        PidQueue pidQueue = new PidQueue();
        pidQueue.setId(id);
        return this.pidQueueMapper.deleteByPrimaryKey(pidQueue);
    }

    /**
     * 获取淘宝商品详情
     *
     * @param pid
     * @return
     */
    @Override
    public CommonResult getDetails(String pid) {
        JSONObject itemInfo = getItemInfo(pid, true);
        // 转换成bean
        if (null != itemInfo && itemInfo.containsKey("item")) {
            ItemDetails itemDetail = new ItemDetails();

            JSONObject itemJson = itemInfo.getJSONObject("item");
            itemDetail.setNum_iid(itemJson.getString("num_iid"));
            itemDetail.setTitle(itemJson.getString("title"));
            itemDetail.setPrice(DataDealUtil.dealTaoBaoPriceAndChange(itemJson.getString("price")));
            itemDetail.setOrginal_price(DataDealUtil.dealTaoBaoPriceAndChange(itemJson.getString("orginal_price")));
            itemDetail.setDetail_url(itemJson.getString("detail_url"));
            itemDetail.setPic_url(itemJson.getString("pic_url"));
            itemDetail.setBrand(itemJson.getString("brand"));
            itemDetail.setRootCatId(itemJson.getString("rootCatId"));
            itemDetail.setCid(itemJson.getString("cid"));
            itemDetail.setDesc(itemJson.getString("desc"));
            itemDetail.setSales(itemJson.getString("sales"));
            itemDetail.setShop_id(itemJson.getString("shop_id"));

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
                        skuClJson.put("price", DataDealUtil.dealTaoBaoPriceAndChange(price));
                        skuClJson.put("orginal_price", DataDealUtil.dealTaoBaoPriceAndChange(orginal_price));
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

    /**
     * 获取速卖通商品详情
     *
     * @param pid
     * @return
     */
    @Override
    public JSONObject getAliexpressDetail(Long pid, boolean isCache) {
        Objects.requireNonNull(pid);

        Callable<JSONObject> callable = () -> getAlibabaOrAliExpressItem(pid, isCache, TypeSite.ALIEXPRESS);

        Retryer<JSONObject> retryer = RetryerBuilder.<JSONObject>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(2000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            return retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 获取阿里巴巴商品详情
     *
     * @param pid
     * @return
     */
    @Override
    public JSONObject getAlibabaDetail(Long pid, boolean isCache) {
        Objects.requireNonNull(pid);

        Callable<JSONObject> callable = () -> getAlibabaOrAliExpressItem(pid, isCache, TypeSite.ALIBABA);

        Retryer<JSONObject> retryer = RetryerBuilder.<JSONObject>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IllegalStateException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(2000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();
        try {
            return retryer.call(callable);
        } catch (RetryException | ExecutionException e) {
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 通过numIid搜索商品（阿里巴巴）详情
     *
     * @param numIid
     * @param isCache
     * @param type
     * @return
     */
    private JSONObject getAlibabaOrAliExpressItem(Long pid, boolean isCache, TypeSite type) {
        Objects.requireNonNull(pid);

        if (isCache) {
            JSONObject itemFromRedis = this.ali1688CacheService.getItemInfo(String.valueOf(pid));
            if (null != itemFromRedis) {
                checkPidInfo(String.valueOf(pid), itemFromRedis);
                return itemFromRedis;
            }
        }

        String url = URL_ITEM_GET_ALIBABA;
        if (type == TypeSite.ALIEXPRESS) {
            url = URL_ITEM_GET_ALIEXPRESS;
        }
        Optional<JSONObject> jsonObjectOpt = UrlUtil.getInstance().callUrlByGet(String.format(url, config.API_HOST, config.API_KEY, config.API_SECRET, pid));
        if (jsonObjectOpt.isPresent()) {
            JSONObject jsonObject = checkResult(pid, jsonObjectOpt.get());
            checkItem(pid, jsonObject);
            this.ali1688CacheService.setItemInfo(String.valueOf(pid), jsonObject);
            return jsonObject;
        } else {
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }

    }

    /**
     * 判断返回json是否有异常信息
     *
     * @param pid
     * @param jsonObject
     * @return
     */
    private JSONObject checkResult(Long pid, JSONObject jsonObject) {
        String error = jsonObject.getString("error");
        if (StringUtils.isNotEmpty(error)) {
            if (error.contains("你的授权已经过期")) {
                throw new BizException(BizErrorCodeEnum.EXPIRE_FAIL);
            } else if (error.contains("超过")) {
                throw new BizException(BizErrorCodeEnum.LIMIT_EXCEED_FAIL);
            } else if (error.contains("item-not-found")) {
                throw new IllegalStateException("item-not-found");
            }
            log.warn("json's error is not empty:[{}]，numIid:[{}]", error, pid);
            jsonObject = InvalidPid.of(pid, error);
        }
        return jsonObject;
    }

    /**
     * 获取淘宝商品详情
     *
     * @param pid
     * @param isCache
     * @return
     */
    private JSONObject getItemInfo(String pid, boolean isCache) {
        Objects.requireNonNull(pid);
        if (isCache) {
            JSONObject itemFromRedis = this.ali1688CacheService.getItemInfo(pid);
            if (null != itemFromRedis) {
                checkPidInfo(pid, itemFromRedis);
                return itemFromRedis;
            }
        }

        Optional<JSONObject> jsonObjectOpt = UrlUtil.getInstance().callUrlByGet(String.format(URL_TAOBAO_ITEM_DETAILS, config.API_KEY, config.API_SECRET, pid));
        if (jsonObjectOpt.isPresent()) {
            String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
            this.redisTemplate.opsForHash().increment(REDIS_TAOBAO_PID_COUNT, "pid_" + strYmd, 1);
            JSONObject jsonObject = this.checkResult(Long.parseLong(pid), jsonObjectOpt.get());
            checkPidInfo(pid, jsonObject);
            this.ali1688CacheService.setItemInfo(pid, jsonObject);
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
            // 保存2小时过期
            this.ali1688CacheService.setItemInfoExpireTime(pid, jsonObject, 2);
            log.warn("itemInfos is null ,pid:[{}]", pid);
            throw new BizException(BizErrorCodeEnum.ITEM_IS_NULL);
        }
    }




    /**
     * fillItems
     *
     * @param lstAllItems
     * @param shopid
     * @param page
     * @return
     * @throws IOException
     */
    private Map<String, Integer> fillItems(List<Ali1688Item> lstAllItems, String shopid, int page) throws IOException {

        Objects.requireNonNull(lstAllItems);
        Objects.requireNonNull(shopid);
        log.info("begin fillItems: shopid:[{}] page:[{}]", shopid, page);

        Map<String, Integer> result = new HashMap<>(3);

        Optional<JSONObject> jsonObjectOpt = UrlUtil.getInstance().callUrlByGet(String.format(URL_ITEM_SEARCH, config.API_HOST, config.API_KEY, config.API_SECRET, shopid, page));

        if (jsonObjectOpt.isPresent()) {
            JSONObject jsonObject = jsonObjectOpt.get();
            String strYmd = LocalDate.now().format(DateTimeFormatter.ofPattern(YYYYMMDD));
            this.redisTemplate.opsForHash().increment(REDIS_CALL_COUNT, "shop_" + strYmd, 1);

            checkReturnJson(jsonObject);

            JSONObject items = jsonObject.getJSONObject("items");
            Ali1688Item[] ali1688Items = JSON.parseObject(items.getJSONArray("item").toJSONString(), Ali1688Item[].class);
            log.info("end fillItems: size:[{}] ", ali1688Items.length);
            lstAllItems.addAll(Arrays.asList(ali1688Items));

            result.put("pagecount", Integer.parseInt(items.getString("pagecount")));
            result.put("total_results", Integer.parseInt(items.getString("total_results")));
            result.put("page_size", Integer.parseInt(items.getString("page_size")));

            return result;
        } else {
            throw new BizException(BizErrorCodeEnum.BODY_IS_NULL);
        }
    }

    /**
     * isHaveData
     *
     * @param jsonObject
     * @return
     */
    private void checkReturnJson(JSONObject jsonObject) {
        Objects.requireNonNull(jsonObject);
        String error = jsonObject.getString("error");
        if (!StringUtils.isEmpty(error)) {
            throw new BizException(BizErrorCodeEnum.FAIL, error);
        }
    }


    /**
     * 校验内容是否正确
     *
     * @param pid
     * @param jsonObject
     */
    private void checkItem(Long pid, JSONObject jsonObject) {
        Objects.requireNonNull(pid);
        Objects.requireNonNull(jsonObject);
        JSONObject item = jsonObject.getJSONObject("item");
        if (item != null) {
            if (StringUtils.isEmpty(item.getString("desc"))) {
                log.warn("desc is null ,pid:[{}]", pid);
                throw new BizException(BizErrorCodeEnum.DESC_IS_NULL);
            }
        }
    }

}
