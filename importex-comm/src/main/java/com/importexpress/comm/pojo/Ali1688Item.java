package com.importexpress.comm.pojo;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author luohao
 * @date 2019/11/5
 */
@Data
public class Ali1688Item {

    private String num_iid;
    private String pic_url;
    private String title;
    private String price;
    private String promotion_price;
    private String volume;
    private String post_fee;
    private String sales;
    private String detail_url;

    public int getSalesOfParse(){
        if(StringUtils.isNotEmpty(this.sales)){
            if(StringUtils.contains(this.sales,"万")){
                return Integer.parseInt(StringUtils.replace(this.sales, "万", "0000"));
            }else if(StringUtils.contains(this.sales,"千")){
                return Integer.parseInt(StringUtils.replace(this.sales, "千", "000"));
            }else if(StringUtils.contains(this.sales,"百")){
                return Integer.parseInt(StringUtils.replace(this.sales, "百", "00"));
            }else{
                return Integer.parseInt(this.sales);
            }
        }else{
            return 0;
        }
    }

}
