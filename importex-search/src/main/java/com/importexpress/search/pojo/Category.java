package com.importexpress.search.pojo;

import lombok.Data;

@Data
public class Category {
	/**
	 * id
	 */
	private String id;

	/**
	 * 类别id
	 */
	private String  catid;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 类别树
	 */
	private String path;
	/**
	 * 级
	 */
	private int level;

	//1没有新品   0有新品
	private int newArrivalsFlag;
	private String newArrivalDate;


	/**
	 * 父类id
	 *
	 */
	private String parentCategory;
}
