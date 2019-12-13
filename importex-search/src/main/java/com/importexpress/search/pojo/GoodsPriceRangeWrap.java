package com.importexpress.search.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsPriceRangeWrap implements Serializable {
    private SearchParam param;
    private GoodsPriceRange range;
    private int total;
    private String minPrice;
    private String maxPrice;
    private int backDiv;

}
