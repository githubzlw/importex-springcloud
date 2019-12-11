package com.importexpress.search.pojo;

import lombok.Data;

/**
 * 属性
 * @author sj
 *
 */
@Data
public class Attribute {

	public Attribute(){}

	public Attribute(String id,String name,String value){
		this.id = id;
		this.name = name;
		this.value = value;
	}
	/**
	 * 属性-值 id
	 */
	private String id;
	/**
	 * 属性
	 */
	private String name;
	/**
	 * 属性值
	 */
	private String value;

	/**
	 * 链接
	 */
	private String url;

}
