package com.importexpress.shopify.pojo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class PageClass {
    private PageInfo pageInfo;
    private List<ImportProductBean> productList;

    public PageClass(PageInfo pageInfo, List<ImportProductBean> productList) {
        this.pageInfo = pageInfo;
        this.productList = productList;
    }
}