package com.importexpress.shopify.component;

import com.google.common.collect.Lists;
import com.importexpress.shopify.pojo.GoodsBean;
import com.importexpress.shopify.pojo.OptionWrap;
import com.importexpress.shopify.pojo.product.Images;
import com.importexpress.shopify.pojo.product.Product;
import com.importexpress.shopify.pojo.product.Variants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ShopifyProduct {
    @Autowired
    private SkuJsonParse skuJsonParse;

    /**
     * @param goods
     * @return
     */
    public Product toProduct(GoodsBean goods){
        Product product = new Product();
        product.setTitle(goods.getName());

        String info_ori = info(goods.getInfoHtml());
        StringBuilder details = details(goods.getInfo());
        details.append(info_ori);
        product.setBody_html(details.toString());

        product.setVendor("www.import-express.com");
        String category = productType(goods.getCategory());
        product.setProduct_type(category);

        List<Variants> lstVariants = skuJsonParse.sku2Variants(goods.getSkuProducts(),
                goods.getType(), goods.getPerWeight(), "kg");
        product.setVariants(lstVariants);

        OptionWrap wrap = skuJsonParse.spec2Options(goods.getType());
        product.setOptions(wrap.getOptions());

        List<Images> lstImages = images(goods.getImage());
        lstImages.addAll(wrap.getLstImages());
        product.setImages(lstImages);
        return product;
    }


    /**图片
     * @param pImage
     * @return
     */
    private List<Images>  images( List<String> pImage){
        List<Images> lstImages = Lists.newArrayList();
        Images images;
        for (int i = 0, size = pImage.size(); i < size; i++) {
            String imgSrc = pImage.get(i).replace(".60x60", ".400x400");
            images = new Images();
            images.setSrc(imgSrc);
            lstImages.add(images);
        }
        return lstImages;
    }

    /**规格
     * @param category
     * @return
     */
    private String productType(String category){
        if (StringUtils.isNotBlank(category)) {
            String[] categorys = category.split("(\\^\\^)");
            category = categorys.length > 1 ? categorys[1] : categorys[0];
            categorys = category.split(">");
            return categorys[categorys.length - 1];
        }
        return "";
    }

    /**详情
     * @param info
     * @return
     */
    private String info(String info){
        if (StringUtils.isNotBlank(info)) {
            info = info.replaceAll("src=\".*/newindex/img/dot.gif\"", "");
            info = info.replace("data-original", "src");
        } else {
            info = "";
        }
        return info;
    }

    /**明细
     * @param detail
     * @return
     */
    private StringBuilder details(Map<String, String> detail){
        StringBuilder sb = new StringBuilder();
        if (detail != null && !detail.isEmpty()) {
            sb.append("<div>");
            Iterator<Map.Entry<String, String>> iterator = detail.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                sb.append("<span>").append(next.getValue()).append("</span>");
            }
            sb.append("</div");
        }
        return sb;
    }
}
