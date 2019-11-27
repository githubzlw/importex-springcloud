package com.importexpress.shopify.pojo;

import lombok.Data;

import java.io.Serializable;
@Data
public class PriceBean implements Serializable{
	private static final long serialVersionUID = 1171899745637043213L;
	private String price;//价格
	private String factoryPrice;//工厂价格
	private String feight;//我司运费
	private String quantity;//数量
	private String priceJ;//5-9
	private String priceE;//9-15
	private int moq;//最小定量
}
