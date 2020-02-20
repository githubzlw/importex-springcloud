package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 类别
 */
@Data
public class Category {
	/**
	 * id
	 */
	@ApiModelProperty(value="类别ID")
	private String id;

	/**
	 * 类别id
	 */
	@ApiModelProperty(value="")
	private String  catid;
	/**
	 * 名称
	 */
	@ApiModelProperty(value="名称")
	private String name;
	/**
	 * 类别树
	 */
	@ApiModelProperty(value="类别树")
	private String path;
	/**
	 * 级
	 */
	@ApiModelProperty(value="类别级")
	private int level;

	/**
	 * 新品表示 1没有新品   0有新品
	 */
	@ApiModelProperty(value="新品表示 1没有新品   0有新品")
	private int newArrivalsFlag;

	/**
	 * 新品日期
	 */
	@ApiModelProperty(value="新品日期")
	private String newArrivalDate;


	/**
	 * 父类id
	 *
	 */
	@ApiModelProperty(value="父类")
	private String parentCategory;
}
