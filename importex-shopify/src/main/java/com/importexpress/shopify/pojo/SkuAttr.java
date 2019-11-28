package com.importexpress.shopify.pojo;

import lombok.Data;

import java.util.List;

@Data
public class SkuAttr {
	private String skuAttr;
	private String skuPropIds;
	private SkuVal skuVal;
    private String specId;
    private String skuId;
    private double fianlWeight;
    private List<String> wholesalePrice;
}
