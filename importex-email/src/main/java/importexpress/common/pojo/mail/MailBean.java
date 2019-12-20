package importexpress.common.pojo.mail;

import com.importexpress.comm.pojo.SiteEnum;
import lombok.Data;

import java.util.Map;

/**
 * @author jack.luo
 * @date 2019/9/5
 */
@Data
public class MailBean {

    /** to*/
    private String to;

    /** bcc*/
    private String bcc;

    /** subject*/
    private String subject;

    /** body*/
    public String body;

    /** 填充键值*/
    private Map<String, String> model;

    /** 邮件类型 */
    private TemplateType templateType;

    /** 1:线上请求    2:线下请求 */
    public int type=1;

    /** true:测试模板（不实际发送邮件） */
    private boolean isTest=true;

    /** 区分网站*/
    private SiteEnum siteEnum;

}
