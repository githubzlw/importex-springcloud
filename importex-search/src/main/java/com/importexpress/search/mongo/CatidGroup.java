package com.importexpress.search.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author luohao
 * @String 2018/11/28
 */
@Data
public class CatidGroup {
    @Id
    private String _id;

    private String catid;

    private String category_name;

    private String num;

}