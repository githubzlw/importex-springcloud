package com.importexpress.comm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.pojo.Ali1688Item;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jack.luo
 * @date 2019/11/4
 */
@Slf4j
public class UrlUtil {

    /**
     * singleton
     */
    private static UrlUtil singleton = null;

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
    private UrlUtil() {

    }

    /**
     * getInstance
     *
     * @return
     */
    public static UrlUtil getInstance() {

        if (singleton == null) {
            synchronized (UrlUtil.class) {
                if (singleton == null) {
                    singleton = new UrlUtil();
                }
            }
        }
        return singleton;
    }

    public static void main(String[] args) {
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

    /**
     * 调用URL（Get）
     *
     * @param url
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByGet(String url) throws IOException {

        log.info("callUrlByGet:{}", url);
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("response is not successful");
        }
        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }

    /**
     * 调用URL（Get）
     *
     * @param url
     * @return
     * @throws IOException
     */
    public boolean isAccessURL(String url) {

        Request request = new Request.Builder().url(url).build();
        try {
            if (client.newCall(request).execute().isSuccessful()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

    }

    /**
     * do post
     *
     * @param url
     * @param tp
     * @param fileName
     * @return
     * @throws IOException
     */
    public JSONObject doPostForImgUpload(String url, String tp, String fileName, String key, String secret) throws IOException {
        log.info("url:{} tp:{} fileName:{}", url, tp, fileName);

        File file = new File(fileName);
        // .addFormDataPart("imgcode", file.getName(),
        //                        RequestBody.create(MediaType.parse("image/jpeg"), file))
        RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("imgcode", file.getName(), body)
                .addFormDataPart("key", key)
                .addFormDataPart("secret", secret)
                .addFormDataPart("api_name", "upload_img")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        // Create a new Call object with put method.
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("response:{}", response);

            throw new IOException("doPostForImgUpload's response is not successful");
        }
        String rs = response.body().string();
        System.err.println(rs);
        return response.body() != null ?
                JSON.parseObject(rs) : null;
    }

    /**
     * callUrlByPut
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByPut(String url, Map<String, String> params) throws IOException {

        log.info("callUrlByPut:{},params:{}", url, params);

        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> {
            if (v != null) builder.add(k, v);
        });
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        return executeCall(url, request);

    }

    /**
     * callUrlByPost
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByPost(String url, Map<String, String> params) throws IOException {

        log.info("callUrlByPost:{},params:{}", url, params);

        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> {
            if (v != null) builder.add(k, v);
        });
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return executeCall(url, request);

    }

    /**
     * call url by retry times
     *
     * @param url
     * @param request
     * @return
     * @throws IOException
     */
    @Nullable
    private JSONObject executeCall(String url, Request request) throws IOException {
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException ioe) {
            //重试15次（每次1秒）
            try {
                int count = 0;
                while (true) {
                    Thread.sleep(1000);
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        log.warn("do retry ,times=[{}]", count);
                    }
                    if (count > 15) {
                        break;
                    }
                    ++count;
                }
            } catch (InterruptedException e) {
            }
        }

        if (response == null || !response.isSuccessful()) {
            log.error("url:[{}]", url);
            throw new IOException("call url is not successful");
        }

        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }

    /**
     * addParamToBuilder
     *
     * @param map
     * @return
     */
    private FormBody.Builder addParamToBuilder(Map<String, Object> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> ite = map.entrySet().iterator();
            for (; ite.hasNext(); ) {
                Map.Entry<String, Object> kv = ite.next();
                builder.add(kv.getKey(), kv.getValue().toString());
            }
        }
        return builder;
    }
}
