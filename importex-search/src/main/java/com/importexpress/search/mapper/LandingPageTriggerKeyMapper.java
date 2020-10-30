package com.importexpress.search.mapper;

import com.importexpress.search.pojo.LimitKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface LandingPageTriggerKeyMapper {
    /**所有
     * @return
     */
    @Select("select trigger_key,advKey,trigger_catid,adgroupid from landing_page_trigger_key ")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "trigger_key", property = "triggerKey"),
            @Result(column = "key", property = "advKey"),
            @Result(column = "trigger_catid", property = "triggerCatid"),
            @Result(column = "adgroupid", property = "adgroupid")
    })
    List<LimitKey> getTriggerKeyList();
}
