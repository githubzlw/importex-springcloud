package com.importexpress.shopify.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: JiangXW
 * @version: v1.0
 * @description: com.importexpress.shopify.mapper
 * @date:2019/12/10
 */
@Component
@Mapper
public interface OverSeaProductMapper {

    @Select("select goods_pid as pid from hot_selling_goods where hot_selling_id in(select id from hot_category where hot_type in(25,26))")
    List<Long> queryOverSeaProductList();
}
