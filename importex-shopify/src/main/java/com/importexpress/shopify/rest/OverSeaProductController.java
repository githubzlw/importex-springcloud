package com.importexpress.shopify.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.importexpress.shopify.pojo.ImportProductBean;
import com.importexpress.shopify.service.OverSeaProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.rest
 * @date:2019/12/10
 */
@Slf4j
@RestController
@RequestMapping("/overSea")
@Api("海外仓商品接口")
public class OverSeaProductController {

    private final OverSeaProductService overSeaProductService;


    public OverSeaProductController(OverSeaProductService overSeaProductService) {
        this.overSeaProductService = overSeaProductService;

    }


    @GetMapping("/list")
    @ApiOperation("获取全部海外仓产品")
    public JSONObject queryForList() {

        JSONObject jsonObject = new JSONObject();
        try {

            List<ImportProductBean> productBeans = overSeaProductService.queryOverSeaProductList();
            if (CollectionUtils.isNotEmpty(productBeans)) {
                jsonObject.put("total", productBeans.size());
                jsonObject.put("rows", JSONArray.toJSON(productBeans));
            } else {
                jsonObject.put("total", 0);
                jsonObject.put("rows", JSONArray.toJSON(new ArrayList<>()));
            }
            jsonObject.put("code", 200);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("queryForList error:", e);
            jsonObject.put("message", "queryForList error:" + e.getMessage());
            jsonObject.put("code", 500);
            jsonObject.put("total", 0);
            jsonObject.put("rows", new ArrayList<>());
        }

        return jsonObject;
    }


}
