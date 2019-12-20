package com.importexpress.search.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchWordWrap implements Serializable,Cloneable {
    private static final long serialVersionUID = -7448833575209315621L;

    private String keyWord;
    private String path;
    @Override
    public SearchWordWrap clone()  {
        SearchWordWrap bean = null;
        try {
            bean = (SearchWordWrap)super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
}
