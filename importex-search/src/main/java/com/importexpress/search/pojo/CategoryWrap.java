package com.importexpress.search.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryWrap implements Serializable {
	private static final long serialVersionUID = 986818736352041314L;
	/**
	 * id
	 */
	private String id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 父类
	 */
	private String parentCategory;
	/**
	 * 子类
	 */
	private List<CategoryWrap> childen;
	/**
	 * 级
	 */
	private int level;

	/**
	 * 链接
	 */
	private String url;

	/**
	 * 选中
	 */
	private int selected;

	/**
	 * 数量
	 */
	private long count;
}
