package com.importexpress.search.pojo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryWrap {
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
	private boolean isSelected;

	/**
	 * 数量
	 */
	private long count;
}
