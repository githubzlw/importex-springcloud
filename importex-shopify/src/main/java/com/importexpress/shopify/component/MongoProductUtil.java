package com.importexpress.shopify.component;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.importexpress.comm.pojo.Product;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.shopify.pojo.ShopifyData;
import com.importexpress.shopify.pojo.TypeBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MongoProductUtil {
    private static final String IMPORT_WEBSITE = "import-express.com";
    private static final String KID_WEBSITE = "kidsproductwholesale.com";
    private static final String PET_WEBSITE = "petstoreinc.com";
//    public static final String HTTP_IMPORT_WEBSITE = "https://www.import-express.com";
//    public static final String HTTP_KID_WEBSITE = "https://www.kidsproductwholesale.com";
//    public static final String HTTP_PET_WEBSITE = "https://www.petstoreinc.com";

    //    private static final String IMPORT_WEBSITE_VIDEOURL_1 = "img1.import-express.com";
//    private static final String IMPORT_WEBSITE_VIDEOURL = "img.import-express.com";
//    private static final String KID_WEBSITE_VIDEOURL = "img1.kidsproductwholesale.com";
//    private static final String PET_WEBSITE_VIDEOURL = "img1.petstoreinc.com";
    private static String chineseChar = "([\\一-\\龥]+)";//()表示匹配字符串，[]表示在首尾字符范围  从 \\一 到 \\龥字符之间，+号表示至少出现一次

    /**
     * mongo 数据转换
     *
     * @param goods
     * @param site
     * @return
     */
    public static ShopifyData composeShopifyData(Product goods, int site) {
        String remotPath = checkIsNullAndReplace(goods.getRemotpath(), site);
        ShopifyData data = new ShopifyData();
        data.setPid(String.valueOf(goods.getPid()));
        data.setInfo(detail(goods));
        data.setInfoHtml(info(goods, remotPath));
        data.setName(goods.getEnname());
        data.setPrice(price(goods));
        data.setImage(image(goods, remotPath));
        data.setType(type(goods, remotPath));
        data.setVendor(vendor(site));
        String weight = goods.getFinal_weight();
        if (StringUtils.isBlank(weight)) {
            weight = goods.getWeight();
            if (StringUtils.isBlank(weight)) {
                weight = goods.getAli_weight();
            }
        }
        String perweight = StrUtils.matchStr(weight, "(\\d+\\.*\\d*)");
        data.setPerWeight(perweight);
        data.setSkuProducts(goods.getSku());
        return data;
    }

    private static String price(Product goods) {
        String range_price = goods.getRange_price();
        if (StringUtils.isBlank(range_price)) {
            String wprice = goods.getWprice();
            if (StringUtils.isBlank(wprice)) {
                return goods.getPrice();
            }
            wprice = wprice.replaceAll("[\\[\\]]", "").trim();
            String[] prices = wprice.split(",\\s*");
            if (prices.length > 0) {
                String[] wholePrices = prices[0].split("(\\$)");
                if (wholePrices.length > 1) {
                    return wholePrices[1].trim();
                }
            }
            return goods.getPrice();
        } else {
            return range_price;
        }
    }

    private static List<String> image(Product goods, String remotPath) {
        List<String> imgList = Lists.newArrayList();
        String img = goods.getImg();
        if (StringUtils.isNotBlank(img)) {
            img = img.replace("[", "")
                    .replace("]", "")
                    .replaceAll("http://", "https://").trim();
            String[] imgs = img.split(",\\s*");
            for (int i = 0; i < imgs.length; i++) {
                if (imgs[i].indexOf("http://") > -1 || imgs[i].indexOf("https://") > -1) {
                    imgList.add(imgs[i].replaceAll("http://", "https://"));
                } else {
                    imgList.add(remotPath + imgs[i]);
                }
            }
        }
        return imgList;
    }


    private static List<String> detail(Product goods) {
        String detail = goods.getEndetail();
        List<String> list = Lists.newArrayList();
        if (StringUtils.isNotBlank(detail) && detail.length() > 2) {
            String[] details = detail.substring(1, detail.length() - 1).split(", ");
            int details_length = details.length;
            for (int i = 0; i < details_length; i++) {
                String str_detail = details[i].trim().replaceAll(chineseChar, "");
                if (str_detail.isEmpty() || StrUtils.isMatch(str_detail.substring(0, 1), "\\d+")) {
                    continue;
                }
                if (StrUtils.isFind(str_detail, "(brand\\:)")) {
                    continue;
                }
                if (str_detail.length() < 2) {
                    continue;
                }
                if (StrUtils.isFind(str_detail, "(([uU][0-9a-f]+){2,})")) {
                    continue;
                }
                list.add(str_detail.substring(0, 1).toUpperCase() + str_detail.substring(1, str_detail.length()));
            }
        }

        return list;
    }

    private static String info(Product goods, String remotPath) {
        String info = goods.getEninfo();
        String isShowDetImgFlag = goods.getIs_show_det_img_flag();
        if (StringUtils.isBlank(info) || !"1".equals(isShowDetImgFlag)) {
            return "";
        }
        Element parse = Jsoup.parse(info).body();
        Elements select = parse.select("img");
        boolean isOldData = remotPath.indexOf("/images/") > -1;
        for (Element s : select) {
            String src = s.attr("src");
            src = isOldData && src.indexOf("http") > -1 && src.indexOf("/desc/") > -1 ?
                    src.split("/desc/")[1] : src;
            if (StringUtils.isBlank(src)) {
                continue;
            }
            if (!isOldData && src.indexOf("http") > -1) {
            } else {
                src = remotPath.replace("/images/", "/desc/") + src;
            }
            s.attr("src", src.replaceAll("http://", "https://"));
        }
        info = parse.toString();
        info = info.replace("<body>", "").replace("</body>", "");
        info = info.replaceAll("http://", "https://");
        return info;
    }

    private static List<TypeBean> type(Product goods, String remotPath) {
        String entypeStr = goods.getEntype_new();
        List<TypeBean> entypeNew = Lists.newArrayList();
        if (StringUtils.isBlank(entypeStr)) {
            return entypeNew;
        }
        try {
            Gson gson = new Gson();
            entypeNew = gson.fromJson(entypeStr, new TypeToken<List<TypeBean>>() {
            }.getType());
            if (entypeNew == null || entypeNew.size() == 0) {
                return Lists.newArrayList();
            }
            entypeNew.stream().forEach(e -> {
                if (StringUtils.isNotBlank(e.getImg())) {
                    e.setImg(remotPath + e.getImg());
                }
                e.setValue(e.getValue().replaceAll("\\|", " "));
            });
//            entypeNew = entypeNew.stream().sorted(Comparator.comparing(TypeBean::getType)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("get entype_new error pid:{} ,e:{}", goods.getPid(), e);
        }
        return entypeNew;
    }


    /**
     * 根据网站设置
     *
     * @return
     */
    private static String vendor(int site) {
        String ven = "";
        switch (site) {
            case 2:
                ven = "www.kidsproductwholesale.com";
                break;
            case 4:
                ven = "www.petstoreinc.com";
                break;
            case 8:
                ven = "www.homeproductimport.com";
                break;
            case 16:
                ven = "www.medicaldevicefactory.com";
                break;
            default:
                ven = "www.import-express.com";
        }
        return ven;
    }

    /**
     * 检查是否未null和替换
     *
     * @param oldStr
     * @return
     */
    public static String checkIsNullAndReplace(String oldStr, int site) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(oldStr)) {
            String tempStr;
            switch (site) {
                case 2:
                    tempStr = oldStr.replace(IMPORT_WEBSITE, KID_WEBSITE);
                    break;
                case 4:
                    tempStr = oldStr.replace(IMPORT_WEBSITE, PET_WEBSITE);
                    break;
                default:
                    tempStr = oldStr;
            }
            return tempStr;
        } else {
            return oldStr;
        }
    }

}
