package com.importexpress.search.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 同义词相关表
 * 1.搜索词同义词synonym
 * 2.类别同义词synonyms_category
 */
@Component
@Mapper
public interface SynonymMapper {

	/**
	 * 初始化同义词列表
	 * @return
	 */
	@Select("SELECT keyword,keyword1 FROM synonym WHERE issyn=1 " +
			"AND isDelete=0 AND keyword1 IS NOT NULL AND keyword IS NOT NULL")
	List<Map<String,String>> getSynonymKeyword();


	/**
	 *大类 小类的 同义词
	 * @return
	 */
	@Select("select category,catid,synonyms_category from synonyms_category where valid=1")
	List<Map<String,String>> getSynonymsCategory();
}