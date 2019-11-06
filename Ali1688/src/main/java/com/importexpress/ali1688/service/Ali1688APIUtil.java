package com.importexpress.ali1688.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.common.pojo.Ali1688Item;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private static final String URL_ITEM_GET = "http://api.onebound.cn/1688/api_call.php?num_iid=%s&cache=no&api_name=item_get&lang=en&key=tel13661551626&secret=20191104";


    private static final String URL_ITEM_SEARCH = "http://api.onebound.cn/1688/api_call.php?seller_nick=%s&start_price=0&end_price=0&q=&page=%d&cid=&cache=no&api_name=item_search_shop&lang=en&key=tel13661551626&secret=20191104";


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
            JSONObject jsonObject = callURLByGet(String.format(URL_ITEM_GET, pid));
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

        List<JSONObject> lstResult = new CopyOnWriteArrayList<JSONObject>();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for(long pid : pids){
            executorService.execute(() -> {
                JSONObject item = Ali1688APIUtil.getInstance().getItem(pid);
                if(item!=null) {
                    lstResult.add(item);
                } else {
                    lstResult.add(getNotExistPid());
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("InterruptedException",e);
        }

        return lstResult;
    }

    public List<Ali1688Item> getItemsInShop(String shopid) {

        try {
            List<Ali1688Item> result = new ArrayList<>(100);

            int count = fillItems(result, shopid, 1);
            if(count!=-1){
                for(int i=2;i<=count;i++){
                    fillItems(result, shopid, i);
                }
                return result;
            }else{
                log.warn("no data shopid:[{}]",shopid);
                return null;
            }

        } catch (IOException e) {
            log.error("getItemsInShop", e);
            return null;
        }
    }

    private int fillItems(List<Ali1688Item> lstAllItems,String shopid,int page) throws IOException {

        log.info("begin fillItems: shopid:[{}] page:[{}]",shopid,page);

        JSONObject jsonObject = callURLByGet(String.format(URL_ITEM_SEARCH, shopid,page));
        if(!isHaveData(jsonObject)){
            return -1;
        }

        JSONObject items = jsonObject.getJSONObject("items");
        Ali1688Item[] ali1688Items = (Ali1688Item[]) JSON.parseObject(items.getJSONArray("item").toJSONString(), Ali1688Item[].class);
        lstAllItems.addAll(Arrays.asList(ali1688Items));

        return  Integer.parseInt(items.getString("pagecount"));

    }

    private boolean isHaveData(JSONObject jsonObject){

        return StringUtils.isEmpty(jsonObject.getString("error"));
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

    public static void main(String[] args){
        String json = "{\n" +
                "\t\"user\": {\n" +
                "\t\t\"id\": \"\",\n" +
                "\t\t\"nick\": \"\",\n" +
                "\t\t\"city\": \"江苏 扬州\",\n" +
                "\t\t\"state\": \"\",\n" +
                "\t\t\"good_num\": \"\",\n" +
                "\t\t\"level\": \"5\",\n" +
                "\t\t\"score\": \"\",\n" +
                "\t\t\"total_num\": \"11\",\n" +
                "\t\t\"created\": \"\",\n" +
                "\t\t\"shop_type\": \"\",\n" +
                "\t\t\"user_num_id\": \"b2b-25396717735e2ts\",\n" +
                "\t\t\"company_id\": \"35613108\",\n" +
                "\t\t\"cid\": \"-1\",\n" +
                "\t\t\"pic_url\": \"\",\n" +
                "\t\t\"delivery_score\": \"\",\n" +
                "\t\t\"item_score\": \"\",\n" +
                "\t\t\"sid\": \"shop1432227742608\",\n" +
                "\t\t\"title\": \"扬州市荒原狼体育用品有限公司\",\n" +
                "\t\t\"address\": \"中国 江苏 扬州市江都区小纪镇富民工业园区\",\n" +
                "\t\t\"phone\": \"\",\n" +
                "\t\t\"zhuy\": \"http://shop1432227742608.1688.com/\",\n" +
                "\t\t\"score_p\": \"\",\n" +
                "\t\t\"biz_type_model\": \"生产厂家\",\n" +
                "\t\t\"checked\": \"\",\n" +
                "\t\t\"sale_level\": \"\",\n" +
                "\t\t\"trade_level\": \"\",\n" +
                "\t\t\"rate_value\": \"\",\n" +
                "\t\t\"contact\": \"王欣\",\n" +
                "\t\t\"menu\": []\n" +
                "\t},\n" +
                "\t\"items\": {\n" +
                "\t\t\"item\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"num_iid\": \"588766108897\",\n" +
                "\t\t\t\t\"pic_url\": \"//cbu01.alicdn.com/img/ibank/2019/473/321/10563123374_2145814650.jpg\",\n" +
                "\t\t\t\t\"title\": \"Elastic band resistance band hip ring fitness squat Yoga hip lifting leg training tension band silicone antiskid\",\n" +
                "\t\t\t\t\"price\": \"8.00\",\n" +
                "\t\t\t\t\"promotion_price\": \"8.00\",\n" +
                "\t\t\t\t\"volume\": \"\",\n" +
                "\t\t\t\t\"post_fee\": \"\",\n" +
                "\t\t\t\t\"sales\": \"618\",\n" +
                "\t\t\t\t\"detail_url\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"num_iid\": \"537476150010\",\n" +
                "\t\t\t\t\"pic_url\": \"//cbu01.alicdn.com/img/ibank/2018/217/069/9384960712_2145814650.jpg\",\n" +
                "\t\t\t\t\"title\": \"Football Leg Protector, Sock Sleeve, Double-layer Air-permeable Mesh, Crus Insertion, Tibia Sock Sleeve, Leg Protector, Elastic Sock Sleeve Fixation\",\n" +
                "\t\t\t\t\"price\": \"7.00\",\n" +
                "\t\t\t\t\"promotion_price\": \"7.00\",\n" +
                "\t\t\t\t\"volume\": \"\",\n" +
                "\t\t\t\t\"post_fee\": \"\",\n" +
                "\t\t\t\t\"sales\": \"723\",\n" +
                "\t\t\t\t\"detail_url\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"num_iid\": \"569364622726\",\n" +
                "\t\t\t\t\"pic_url\": \"//cbu01.alicdn.com/img/ibank/2018/135/357/8857753531_2145814650.jpg\",\n" +
                "\t\t\t\t\"title\": \"Sports elbow protector nylon jacquard bandage men and women fitness badminton basketball tennis elbow protector manufacturers wholesale custom-made\",\n" +
                "\t\t\t\t\"price\": \"7.50\",\n" +
                "\t\t\t\t\"promotion_price\": \"7.50\",\n" +
                "\t\t\t\t\"volume\": \"\",\n" +
                "\t\t\t\t\"post_fee\": \"\",\n" +
                "\t\t\t\t\"sales\": \"553\",\n" +
                "\t\t\t\t\"detail_url\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"num_iid\": \"592787838797\",\n" +
                "\t\t\t\t\"pic_url\": \"//cbu01.alicdn.com/img/ibank/2019/187/508/10880805781_2145814650.jpg\",\n" +
                "\t\t\t\t\"title\": \"Yoga pull with hip ring resistance with hip ring masochism ring squatting fitness with resistance ring elastic ring supplies\",\n" +
                "\t\t\t\t\"price\": \"9.50\",\n" +
                "\t\t\t\t\"promotion_price\": \"9.50\",\n" +
                "\t\t\t\t\"volume\": \"\",\n" +
                "\t\t\t\t\"post_fee\": \"\",\n" +
                "\t\t\t\t\"sales\": \"96\",\n" +
                "\t\t\t\t\"detail_url\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"num_iid\": \"567904615255\",\n" +
                "\t\t\t\t\"pic_url\": \"//cbu01.alicdn.com/img/ibank/2018/654/592/8776295456_2145814650.jpg\",\n" +
                "\t\t\t\t\"title\": \"Sports nylon jacquard knee strap pressure badminton basketball fitness knitting protective equipment cross border Custom Decal\",\n" +
                "\t\t\t\t\"price\": \"9.50\",\n" +
                "\t\t\t\t\"promotion_price\": \"9.50\",\n" +
                "\t\t\t\t\"volume\": \"\",\n" +
                "\t\t\t\t\"post_fee\": \"\",\n" +
                "\t\t\t\t\"sales\": \"170\",\n" +
                "\t\t\t\t\"detail_url\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"num_iid\": \"554566334306\",\n" +
                "\t\t\t\t\"pic_url\": \"//cbu01.alicdn.com/img/ibank/2017/698/712/4380217896_2145814650.jpg\",\n" +
                "\t\t\t\t\"title\": \"Dance kneepad thickened all black knee protector fitness running Yoga kneeling kneeling kneepad manufacturer direct customized\",\n" +
                "\t\t\t\t\"price\": \"15.00\",\n" +
                "\t\t\t\t\"promotion_price\": \"15.00\",\n" +
                "\t\t\t\t\"volume\": \"\",\n" +
                "\t\t\t\t\"post_fee\": \"\",\n" +
                "\t\t\t\t\"sales\": \"24\",\n" +
                "\t\t\t\t\"detail_url\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"num_iid\": \"596723120970\",\n" +
                "\t\t\t\t\"pic_url\": \"//cbu01.alicdn.com/img/ibank/2019/522/399/11271993225_2145814650.jpg\",\n" +
                "\t\t\t\t\"title\": \"Weightlifting palm protector exercise dumbbell hard pull iron training half-finger palm protector wear-resistant and slip-resistant customized\",\n" +
                "\t\t\t\t\"price\": \"14.00\",\n" +
                "\t\t\t\t\"promotion_price\": \"14.00\",\n" +
                "\t\t\t\t\"volume\": \"\",\n" +
                "\t\t\t\t\"post_fee\": \"\",\n" +
                "\t\t\t\t\"sales\": \"26\",\n" +
                "\t\t\t\t\"detail_url\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"num_iid\": \"595221603339\",\n" +
                "\t\t\t\t\"pic_url\": \"//cbu01.alicdn.com/img/ibank/2019/272/405/11055504272_2145814650.jpg\",\n" +
                "\t\t\t\t\"title\": \"Hip lifting ring polyester cotton body building resistance belt pull latex ring Yoga hip training products star pattern customization\",\n" +
                "\t\t\t\t\"price\": \"10.00\",\n" +
                "\t\t\t\t\"promotion_price\": \"10.00\",\n" +
                "\t\t\t\t\"volume\": \"\",\n" +
                "\t\t\t\t\"post_fee\": \"\",\n" +
                "\t\t\t\t\"sales\": \"74\",\n" +
                "\t\t\t\t\"detail_url\": \"\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\t\"total_results\": \"169\",\n" +
                "\t\t\"page_size\": 8,\n" +
                "\t\t\"pagecount\": 22,\n" +
                "\t\t\"page\": \"1\",\n" +
                "\t\t\"url\": \"https://m.1688.com/winport/asyncView?ctoken=&memberId=b2b-25396717735e2ts&hasFooter=false&show_search=false&not_fixed=true&_async_id=offerlist%3Aview&_=1542731468687&pageIndex=1\"\n" +
                "\t},\n" +
                "\t\"error\": null,\n" +
                "\t\"secache\": \"d10e105ce2e26ad7c916b6c59874ab4c\",\n" +
                "\t\"secache_time\": 1572511120,\n" +
                "\t\"secache_date\": \"2019-10-31 16:38:40\",\n" +
                "\t\"translate_status\": \"ok\",\n" +
                "\t\"translate_time\": 0.032,\n" +
                "\t\"language\": {\n" +
                "\t\t\"current_lang\": \"en\",\n" +
                "\t\t\"source_lang\": \"cn\"\n" +
                "\t},\n" +
                "\t\"cache\": 1,\n" +
                "\t\"api_info\": \"today:1312 max:2000\",\n" +
                "\t\"execution_time\": 0.068,\n" +
                "\t\"server_time\": \"Beijing/2019-11-05 16:57:28\",\n" +
                "\t\"call_args\": {\n" +
                "\t\t\"seller_nick\": \"shop1432227742608\",\n" +
                "\t\t\"start_price\": \"0\",\n" +
                "\t\t\"end_price\": \"0\",\n" +
                "\t\t\"page\": \"1\"\n" +
                "\t},\n" +
                "\t\"api_type\": \"1688\",\n" +
                "\t\"translate_language\": \"en\",\n" +
                "\t\"translate_engine\": \"baidu\",\n" +
                "\t\"request_id\": \".23778625.5dc139783e0f59.56499715\"\n" +
                "}";
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject items = jsonObject.getJSONObject("items");
        Ali1688Item[] ali1688Item = (Ali1688Item[]) JSON.parseObject(items.getJSONArray("item").toJSONString(), Ali1688Item[].class);
        System.out.println("ali1688Item = " + ali1688Item);
    }
}
