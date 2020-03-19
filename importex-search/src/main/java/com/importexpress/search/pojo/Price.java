package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Price implements Serializable{
	private static final long serialVersionUID = 1171899745637043213L;
	/**
	 * 价格--免邮
	 */
	@ApiModelProperty(value="价格--免邮")
	private String price;
	/**
	 * 工厂价格--非免邮
	 */
	@ApiModelProperty(value="工厂价格--非免邮")
	private String factoryPrice;
	/**
	 * 我司运费
	 */
	@ApiModelProperty(value="我司运费")
	String feight;
	/**
	 * 数量
	 */
	@ApiModelProperty(value="数量")
	private String quantity;
	/**
	 * 最小定量
	 */
	@ApiModelProperty(value="最小定量")
	private int moq;


	public Price(String price,String factoryPrice, String quantity) {
		super();
		this.price = price;
		this.factoryPrice = factoryPrice;
		this.quantity = quantity;
	}
	public Price() {
		super();
	}
}
