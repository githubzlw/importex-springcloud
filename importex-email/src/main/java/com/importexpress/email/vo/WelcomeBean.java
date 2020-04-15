package com.importexpress.email.vo;

import com.importexpress.comm.pojo.SiteEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @Author jack.luo
 * @create 2020/4/15 11:22
 * Description
 */
@Builder
@Data
public class WelcomeBean extends AbstractMailBean{

    private SiteEnum siteEnum;
    private String toEmail;
    private String name;
    private String pass;
    private String from;
    private String activationCode;
    private boolean test;
}
