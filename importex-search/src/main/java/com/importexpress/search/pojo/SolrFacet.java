package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SolrFacet {

	/**
	 * 设置分组
	 */
	@ApiModelProperty(value="设置分组")
	private String facet = "on";

	/**
	 * 设置需要facet的字段
	 */
	@ApiModelProperty(value="设置需要facet的字段")
	private String facetField;

	/**
	 * 返回数量
	 */
	@ApiModelProperty(value="返回数量")
	private int facetLimit;

	/**
	 * 是否统计字段值为null的记录
	 */
	@ApiModelProperty(value="是否统计字段值为null的记录")
	private boolean facetMissing = false;

	/**
	 * 最小次数
	 */
	@ApiModelProperty(value="最小次数")
	private int facetMincount;

	public SolrFacet(String facet, String facetField, boolean facetMissing) {
		super();
		this.facet = facet;
		this.facetField = facetField;
		this.facetMissing = facetMissing;
	}
	public SolrFacet(String facetField, int facetMincount, int facetLimit) {
		super();
		this.facetField = facetField;
		this.facetMincount = facetMincount;
		this.facetLimit = facetLimit;
	}

	public SolrFacet(String facet, String facetField, int facetLimit, boolean facetMissing, int facetMincount) {
		super();
		this.facet = facet;
		this.facetField = facetField;
		this.facetLimit = facetLimit;
		this.facetMissing = facetMissing;
		this.facetMincount = facetMincount;
	}

}
