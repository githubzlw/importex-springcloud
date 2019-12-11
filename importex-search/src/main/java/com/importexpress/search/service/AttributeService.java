package com.importexpress.search.service;

import com.importexpress.search.pojo.Attribute;
import com.importexpress.search.pojo.AttributeWrap;
import com.importexpress.search.pojo.SearchParam;
import org.apache.solr.client.solrj.response.FacetField;

import java.util.List;
import java.util.Map;

/**属性
 * @author Administrator
 *
 */
public interface AttributeService {

	/**搜索属性列表
	 * @param param 搜索参数
	 * @param facetFields solr统计的属性结果
	 * @return
	 */
	List<AttributeWrap> attributes(SearchParam param, List<FacetField> facetFields);


	/**已选择属性
	 * @param param 搜索参数
	 * @return
	 */
	List<Attribute> selectedAttributes(SearchParam param);

	/**数据表读取属性列表
	 * @return
	 */
	Map<String,Attribute> getAttributes();
}
