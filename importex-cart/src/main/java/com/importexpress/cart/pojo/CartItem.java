package com.importexpress.cart.pojo;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author jack.luo
 * @date 2019/12/16
 */
@Data
@Slf4j
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private long pid;

    private long subId1;

    private long subId2;

    /**
     * 店铺id
     */
    private String shoipId;

    /**重量 */
    private float wei;

    /**是否有货 */
    private Boolean isHave = true;

    /**价格 */
    private BigDecimal price;

    /**区间价格 */
    private String wPrice;

    /**规格 */
    private String typeName;

    /**数量 */
    private long num;

    /**是否勾选 1：勾选 0：未勾选 -1：无效 */
    private int checked;

    /**名称 */
    private String name;

    /**图片路径 */
    private String img;

    public String getItemId(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.pid).append(":");
        sb.append(this.subId1);
        if(this.subId2 > 0){
            sb.append(":").append(this.subId2);
        }
        return sb.toString();
    }

}
