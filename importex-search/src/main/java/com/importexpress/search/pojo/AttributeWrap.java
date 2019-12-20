package com.importexpress.search.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AttributeWrap implements Serializable {
	private static final long serialVersionUID = 986818736352041314L;
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
