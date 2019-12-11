package com.importexpress.search.pojo;

import lombok.Data;
import org.apache.solr.client.solrj.response.FacetField;

import java.util.List;

@Data
public class SolrResult {
	//商品列表
	private List<Product> itemList;
	//分组统计列表
	private List<FacetField> categoryFacet;
	//总记录数
	private long recordCount = 0L;
	//规格属性
	private List<FacetField> attrFacet;
}
