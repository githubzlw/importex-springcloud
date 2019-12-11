package com.importexpress.search.mapper;

import com.importexpress.search.pojo.Attribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 属性表 1688_attributes
 */
@Component
@Mapper
public interface AttributeMapper {

	/**获取新的属性列表
	 * @return
	 */
	@Select("select param,value,pvid from 1688_attributes")
	@Results({@Result(column = "param", property = "name"),
			@Result(column = "value", property = "value"),
			@Result(column = "pvid", property = "id")})
	List<Attribute> getAttributes();

}
