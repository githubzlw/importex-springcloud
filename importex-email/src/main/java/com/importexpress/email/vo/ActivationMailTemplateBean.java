package com.importexpress.email.vo;

import com.importexpress.comm.pojo.MailTemplateBean;
import lombok.Data;

/**
 * @Author jack.luo
 * @create 2020/4/15 11:22
 * Description
 */
@Data
public class ActivationMailTemplateBean extends MailTemplateBean {


    private String activationCode;
    private String name;
    private String pass;
    private String from;

}
