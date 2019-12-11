package com.importexpress.search.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
@Mapper
public interface KeywordRecordMapper {
	/**获取关键词类别列表
	 * @return
	 * @data 2018年1月8日
	 * @author user4
	 */
	@Select("SELECT  keyword,catid1,catid2,catid3 FROM  synonym " +
			" WHERE (catid1 REGEXP '[0-9]+' OR catid2 REGEXP '[0-9]+' " +
			"OR catid3 REGEXP '[0-9]+') AND isDelete=0 and issyn=1")
	List<Map<String,String>> getKeywordCatidList();

	/**
	 * 初始化搜索优先类别列表
	 * @return
	 */
	@Select("select keyword,category from priority_category where status=0")
	List<Map<String,String>> getPriorityCategoryList();
	/**初始化限定类别搜索类别列表
	 * @return
	 */
	@Select("select catid,type from  search_special where valid=1")
	List<Map<String,Object>> getSpecialCategoryList();
	/**
	 *初始化搜索词对应的最低价和最高价
	 * @return
	 */
	@Select("SELECT keyword,CONCAT(IFNULL(minPrice,'-'),'@',IFNULL(maxPrice,'-')) " +
			"as price FROM priority_category where status=0")
	List<Map<String,String>> getCategoryPriceList();
}