package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AttributeWrap implements Serializable {
	private static final long serialVersionUID = 986818736352041314L;
	/**
	 * 属性id
	 */
	@ApiModelProperty(value="属性id")
	private String id;

	/**
	 * 名称
	 */
	@ApiModelProperty(value="名称")
	private String name;

	/**
	 * 值列表
	 */
	@ApiModelProperty(value="值列表")
	private List<Attribute> attrs;

}
