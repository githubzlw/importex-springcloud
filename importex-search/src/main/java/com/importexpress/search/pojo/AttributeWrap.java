package com.importexpress.search.pojo;

import lombok.Data;

import java.util.List;

@Data
public class AttributeWrap {

	/**
	 * 属性id
	 */
	private String id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 值列表
	 */
	private List<Attribute> attrs;

}
