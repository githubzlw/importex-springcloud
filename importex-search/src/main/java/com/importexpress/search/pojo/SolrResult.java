package com.importexpress.search.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.solr.client.solrj.response.FacetField;

import java.util.List;

@Data
public class SolrResult {
	/**
	 *商品列表
	 */
	@ApiModelProperty(value="商品列表")
	private List<Product> itemList;
	/**
	 *分组统计列表
	 */
	@ApiModelProperty(value="分组统计列表")
	private List<FacetField> categoryFacet;
	/**
	 *总记录数
	 */
	@ApiModelProperty(value="总记录数")
	private long recordCount = 0L;
	/**
	 * 规格属性
	 */
	@ApiModelProperty(value="规格属性")
	private List<FacetField> attrFacet;
}
