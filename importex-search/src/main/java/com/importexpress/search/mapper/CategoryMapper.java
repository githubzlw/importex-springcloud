/**
 * @ClassName:     AliCategoryMapper.java
 * @Description:   获取商品类别
 *
 * @author         zsl
 * @Date           2016年4月19日 下午4:32:42
 */
package com.importexpress.search.mapper;

import com.importexpress.search.pojo.Category;
import com.importexpress.search.pojo.SearchWordWrap;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类别表 1688_category
 * @author Administrator
 *
 */
@Component
@Mapper
public interface CategoryMapper {

	/**
	 *
	 * @Title
	 * @Description 查询全部的数据
	 * @return List<Category>
	 */
	@Select("select id,category_id,en_name,path,lv,new_arrivals_flag,new_arrival_date " +
			"from change_1688_category where flag=0 and en_name !='' and en_name is not null")
	@Results({@Result(column = "id", property = "id"),
			@Result(column = "category_id", property = "catid"),
			@Result(column = "path", property = "path"),
			@Result(column = "en_name", property = "name"),
			@Result(column = "lv", property = "level"),
			@Result(column = "new_arrivals_flag", property = "newArrivalsFlag"),
			@Result(column = "new_arrival_date", property = "newArrivalDate")})
	List<Category> getCategories();

	@Select("SELECT key_word,path FROM search_words WHERE flag=0 AND path !='' ORDER BY path ASC")
	@Results({
			@Result(column = "key_word", property = "keyWord"),
			@Result(column = "path", property = "path")
	})
	List<SearchWordWrap> getRecommendedWords();

	/**
	 * 查询
	 */
	@Select({
			"<script>" +
					"select id,category_id,en_name,path,lv " +
					"from change_1688_category where category_id in " +
					"<foreach collection='ids' item='item' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach>" +
					"</script>"
	})
	@Results({@Result(column = "id", property = "id"),
			@Result(column = "category_id", property = "catid"),
			@Result(column = "path", property = "path"),
			@Result(column = "en_name", property = "name"),
			@Result(column = "lv", property = "level")})
	List<Category> getCategoriesByIds(@Param("ids") List<String> ids);
}
