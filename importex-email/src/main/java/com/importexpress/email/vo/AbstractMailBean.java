package com.importexpress.email.vo;

import com.importexpress.comm.pojo.SiteEnum;
import lombok.Data;

/**
 * @Author jack.luo
 * @create 2020/4/15 11:24
 * Description
 */
@Data
public abstract class AbstractMailBean {

    private SiteEnum siteEnum;
    private boolean test;
}
