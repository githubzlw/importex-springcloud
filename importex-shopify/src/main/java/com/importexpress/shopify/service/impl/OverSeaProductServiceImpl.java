package com.importexpress.shopify.service.impl;

import com.importexpress.shopify.mapper.OverSeaProductMapper;
import com.importexpress.shopify.pojo.ImportProductBean;
import com.importexpress.shopify.pojo.MongoProduct;
import com.importexpress.shopify.service.OverSeaProductService;
import com.importexpress.shopify.util.MongoUtil;
import com.importexpress.shopify.util.ProductPriceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.service.impl
 * @date:2019/12/10
 */
@Slf4j
@Service
public class OverSeaProductServiceImpl implements OverSeaProductService {

    private final OverSeaProductMapper overSeaProductMapper;

    private final MongoUtil mongoUtil;

    public OverSeaProductServiceImpl(OverSeaProductMapper overSeaProductMapper, MongoUtil mongoUtil) {
        this.overSeaProductMapper = overSeaProductMapper;
        this.mongoUtil = mongoUtil;
    }

    @Override
    public List<ImportProductBean> queryOverSeaProductList() {
        List<Long> pidList = overSeaProductMapper.queryOverSeaProductList();
        List<MongoProduct> tempList = mongoUtil.queryProductList(pidList, 1);
        pidList.clear();

        List<ImportProductBean> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tempList)) {
            tempList.forEach(e -> {
                ImportProductBean.ImportProductBeanBuilder builder = ImportProductBean.builder();
                builder.pid(String.valueOf(e.getPid())).catid(e.getCatid1()).shop_id(e.getShop_id());
                builder.wprice(e.getWprice()).feeprice(e.getFeeprice()).range_price(e.getRange_price())
                        .range_price_free(e.getRange_price_free());

                builder.final_weight(e.getFinal_weight()).morder(e.getMorder()).sellunit(e.getSellunit())
                        .entype_new(e.getEntype_new()).sold(e.getSold());

                if (e.getCustom_main_image().contains("http://")) {
                    builder.custom_main_image(e.getCustom_main_image().replace("http://", "https://"));
                } else if (e.getCustom_main_image().contains("https://")) {
                    builder.custom_main_image(e.getCustom_main_image());
                } else {
                    builder.custom_main_image(e.getRemotpath() + e.getCustom_main_image());
                }

                if (StringUtils.isNotBlank(e.getFinalName())) {
                    builder.enname(e.getFinalName());
                } else {
                    builder.enname(e.getEnname());
                }
                builder.goodsUrl("/goodsinfo/" + e.getEnname().trim().replace(" ", "-") + "-8" + e.getPid() + ".html");
                resultList.add(builder.build());

            });
            resultList.forEach(ProductPriceUtil::setOverSeaPrice);
        }

        return resultList;
    }
}
