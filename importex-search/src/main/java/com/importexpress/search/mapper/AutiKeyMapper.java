package com.importexpress.search.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 反关键词表 anti_key_words
 */
@Component
@Mapper
public interface AutiKeyMapper {
    /**
     * 获取搜索词对应的反关键词
     * @Title getAutiKeyList
     * @Description TODO
     * @return List<Map<String, String>>
     */
    @Select(" select keyword,auti_word,flag from anti_key_words where flag=0")
    List<Map<String, String>> getAutiKeyList();


}
