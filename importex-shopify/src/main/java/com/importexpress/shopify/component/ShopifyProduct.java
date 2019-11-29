package com.importexpress.shopify.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.importexpress.shopify.pojo.OptionWrap;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.product.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class ShopifyProduct {
    @Autowired
    private SkuJsonParse skuJsonParse;

    /**
     * @param goods
     * @return
     */
    public Product toProduct(ShopifyData goods){
        Product product = new Product();
        product.setTitle(goods.getName());

        String info_ori = info(goods.getInfoHtml());
        StringBuilder details = details(goods.getInfo());
        details.append(info_ori);
        product.setBody_html(details.toString());

        product.setVendor(goods.getVendor());
        String category = productType(goods.getCategory());
        product.setProduct_type(category);

        List<Variants> lstVariants = skuJsonParse.sku2Variants(goods.getSkuProducts(),
                goods.getType(), goods.getPerWeight(), "kg");
        if(lstVariants.isEmpty()){
            Variants variant = variant(goods.getPrice(),goods.getPerWeight());
            lstVariants.add(variant);
        }
        product.setVariants(lstVariants);

        OptionWrap wrap = skuJsonParse.spec2Options(goods.getType());
        product.setOptions(wrap.getOptions());
        List<String> lstImg = goods.getImage();
        lstImg.addAll(wrap.getLstImages());
        List<Images> lstImages = images(lstImg);
        product.setImages(lstImages);
        return product;
    }

    private Variants variant(String goodsPrice,String weight){
        goodsPrice  = goodsPrice.split("-")[0];
        Variants variants = new Variants();
        variants.setPrice(goodsPrice);
        variants.setRequires_shipping(true);
        variants.setWeight(weight);
        variants.setWeight_unit("kg");
        variants.setCountry_code_of_origin("CN");
        variants.setInventory_policy("deny");
        variants.setInventory_quantity(999);
        variants.setInventory_management("shopify");
        List<PresentmentPrices> presentment_prices = Lists.newArrayList();
        PresentmentPrices prices = new PresentmentPrices();
        prices.setCompare_at_price(null);
        Price price = new Price();
        price.setAmount(goodsPrice);
        price.setCurrency_code("USD");
        prices.setPrice(price);
        presentment_prices.add(prices);
        variants.setPresentment_prices(presentment_prices);
        return variants;
    }

    /**图片
     * @param pImage
     * @return
     */
    private List<Images>  images( List<String> pImage){
        List<Images> lstImages = Lists.newArrayList();
        Set<String> setImage = Sets.newHashSet(pImage);
        Images images;
        Iterator<String> iterator = setImage.iterator();
        while (iterator.hasNext()) {
            String imgSrc = iterator.next().replace(".60x60", ".400x400");
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
                sb.append("<span style=\"margin-left: 10px;\">").append(next.getValue()).append("</span><br>");
            }
            sb.append("</div");
        }
        return sb;
    }
}
