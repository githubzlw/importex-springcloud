package com.importexpress.search.service.base;

import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.pojo.SolrFacet;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;

@Slf4j
public abstract class SolrBase {

	/**设置Fact
	 * @param solrParams
	 * @param facet
	 * @return
	 */
	public void setFacet(ModifiableSolrParams solrParams, SolrFacet facet){
		//设置分组
		solrParams.set("facet", facet.getFacet());
		//设置需要facet的字段
		solrParams.set("facet.field", facet.getFacetField());
		//返回数量
		solrParams.set("facet.limit", facet.getFacetLimit());
		//是否统计字段值为null的记录
		solrParams.set("facet.missing", facet.isFacetMissing());
		//最小次数
		solrParams.set("facet.mincount", facet.getFacetMincount());
	}


	/**设置row
	 * @param param
	 * @param solrParams
	 */
	public void setRows(SearchParam param,ModifiableSolrParams solrParams){
		int page = Math.max(1,param.getPage());
		param.setPage(page);
		int rows = Math.max(param.getPageSize(),1);
		param.setPageSize(rows);
		setRows((page - 1) * rows,rows,solrParams);
	}
	/**设置row
	 * @param start
	 * @param rows
	 * @param solrParams
	 */
	public void setRows(int start,int rows,ModifiableSolrParams solrParams){
		solrParams.set("start",start);
		solrParams.set("rows",rows);
	}

	/**设置q
	 * @param q
	 * @param solrParams
	 */
	public void setQ(String q,ModifiableSolrParams solrParams){
		solrParams.set("q",q);
	}
	/**设置fq
	 * @param fl
	 * @param solrParams
	 */
	public void setFL(String fl,ModifiableSolrParams solrParams){
		solrParams.set("fl",fl);
	}
	/**设置fq
	 * @param df
	 * @param solrParams
	 */
	public void setDF(String df,ModifiableSolrParams solrParams){
		solrParams.set("df",df);
	}
	/**设置fq
	 * @param fq
	 * @param solrParams
	 */
	public void setFQ(String fq,ModifiableSolrParams solrParams){
		solrParams.set("fq",fq);
	}
	/**设置sort
	 * @param sort
	 * @param solrParams
	 */
	public void setSort(String sort,ModifiableSolrParams solrParams){
		solrParams.set("sort",sort);
	}


	/**发送请求
	 * @param solrParams
	 * @return
	 */
	public QueryResponse sendRequest(ModifiableSolrParams solrParams,HttpSolrClient httpSolrClient) {
		QueryResponse response = null;
		try {
			response = httpSolrClient.query(solrParams,SolrRequest.METHOD.POST);
		}catch (Exception e){
			log.error("solr request error",e);
		}
		return response;
	}
}
