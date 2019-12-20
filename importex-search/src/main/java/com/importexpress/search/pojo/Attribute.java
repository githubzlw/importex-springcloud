package com.importexpress.search.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 属性
 * @author sj
 *
 */
@Data
public class Attribute implements Serializable {
	private static final long serialVersionUID = 986818736352041314L;
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
