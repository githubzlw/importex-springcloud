package com.importexpress.search.pojo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@Builder
public class KeyToCategoryWrap {
    private List<String> lstCatid;
    private String keyword;

}
