package com.importexpress.search.pojo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryWrap implements Serializable {
	private static final long serialVersionUID = 986818736352041314L;
	/**
	 * 类别id
	 */
	@ApiModelProperty(value="类别id")
	private String id;
	/**
	 * 名称
	 */
	@ApiModelProperty(value="名称")
	private String name;
	/**
	 * 父类
	 */
	@ApiModelProperty(value="父类")
	private String parentCategory;
	/**
	 * 子类
	 */
	@ApiModelProperty(value="子类")
	private List<CategoryWrap> childen = Lists.newArrayList();
	/**
	 * 级
	 */
	@ApiModelProperty(value="类别级")
	private int level;

	/**
	 * 链接
	 */
	@ApiModelProperty(value="链接")
	private String url;

	/**
	 * 选中
	 */
	@ApiModelProperty(value="是否选中 1-选中 0-未选中")
	private int selected;

	/**
	 * 数量
	 */
	@ApiModelProperty(value="数量")
	private long count;
}
