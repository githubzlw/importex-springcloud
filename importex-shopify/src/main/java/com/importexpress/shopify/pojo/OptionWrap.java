package com.importexpress.shopify.pojo;

import com.importexpress.shopify.pojo.product.Images;
import com.importexpress.shopify.pojo.product.Options;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class OptionWrap {
    private List<Images> lstImages;
    private List<Options> options;
}
