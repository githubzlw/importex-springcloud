package com.importexpress.shopify.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.importexpress.shopify.pojo.OptionWrap;
import com.importexpress.shopify.pojo.SkuAttr;
import com.importexpress.shopify.pojo.SkuVal;
import com.importexpress.shopify.pojo.TypeBean;
import com.importexpress.shopify.pojo.product.*;
import com.importexpress.comm.util.StrUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * *****************************************************************************************
 * Description:
 * 产品规格属性操作类
 * @author:ShenJing
 * @date:2018年4月28日
 * @version 1.0
 *
 *
 * Version    Date                ModifiedBy                 Content
 * --------   ---------           ----------                -----------------------
 *  1.1.0     2018年5月3日                                  ShenJing                  规格格式更改为原购物车格式
 *******************************************************************************************
 */
@Component
@Slf4j
public class SkuJsonParse {

	/**sku字符串解析成对象数据
	 * @param skuProducts
	 * @return
	 * @data 2018年4月17日
	 * @author user4
	 */
	public Map<String,SkuAttr> parseSku(String skuProducts){
		if(StringUtils.isBlank(skuProducts)){
			return null;
		}
		Map<String,SkuAttr> skuMap = new HashMap<String, SkuAttr>(1000);
		boolean isShipFrom = StrUtils.isFind(skuProducts, "(201336100)");
		SkuVal skuValBean = null;
		SkuAttr skuAttrBean = null;
		JSONArray skuProductsArray;
		try {
			skuProductsArray = JSONArray.fromObject(skuProducts);
			for(int i=0;i<skuProductsArray.size();i++){
				JSONObject skuProductsObject = JSONObject.fromObject(StrUtils.object2Str(skuProductsArray.get(i)));
				String skuPropIds = skuProductsObject.getString("skuPropIds");
				if(isShipFrom && !StrUtils.isFind(","+skuPropIds+",", "(,201336100,)")){
					continue;
				}
				skuAttrBean = new SkuAttr();
				skuAttrBean.setSkuPropIds(skuPropIds);
				skuAttrBean.setSkuAttr(skuProductsObject.getString("skuAttr"));
				JSONObject skuValObject = JSONObject.fromObject(StrUtils.object2Str(skuProductsObject.get("skuVal")));
				String actSkuCalPrice = StrUtils.object2Str(skuValObject.get("actSkuCalPrice"));
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						StrUtils.object2Str(skuValObject.get("actSkuPrice")) : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						StrUtils.object2Str(skuValObject.get("actSkuMultiCurrencyDisplayPrice")) : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						StrUtils.object2Str(skuValObject.get("skuMultiCurrencyDisplayPrice")) : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						StrUtils.object2Str(skuValObject.get("skuPrice")) : actSkuCalPrice;
				skuValBean = new SkuVal();
				skuValBean.setActSkuCalPrice(actSkuCalPrice);
				skuValBean.setActivity(skuValObject.getBoolean("isActivity"));
				skuValBean.setAvailQuantity(skuValObject.getInt("availQuantity"));
				skuAttrBean.setSkuVal(skuValBean);
				skuMap.put(skuPropIds, skuAttrBean);
			}
		} catch (Exception e) {
			log.error("SkuJsonParse->parseSku:"+e.getMessage());
		}finally {
			skuValBean = null;
			skuAttrBean = null;
			skuProductsArray = null;
		}

		return skuMap;
	}



    /**我司产品规格属性Type转成shopify规格属性Options
     * @param typeList
     * @return
     */
    public OptionWrap spec2Options(List<TypeBean> typeList){
    	if(typeList == null || typeList.isEmpty()) {
    		return OptionWrap.builder().lstImages(Lists.newArrayList()).options(Lists.newArrayList()).build();
    	}
    	List<Options> lstOptions = Lists.newArrayList();

		//规格数据
		List<String> type1=Lists.newArrayList(),type2=Lists.newArrayList(),type3=Lists.newArrayList();
		String typeName1 = "",typeName2 = "",typeName3 = "";
		List<String> image = Lists.newArrayList();
		Map<String,String> markMap = Maps.newHashMap();
		int typeIndex = 1;
		Images img;
		for(TypeBean typeBean : typeList){
			String type = typeBean.getType();
			type = type.endsWith(":") ? type.substring(0, type.length() - 1) : type;
			typeBean.setType(type);

			String markType = markMap.get(type);
			markType = markType==null ? "type"+(typeIndex++) : markType;
			markMap.put(type, markType);

			if(StringUtils.isNotBlank(typeBean.getImg())){
				image.add(typeBean.getImg().replace(".60x60", ".400x400"));
			}
			if("type1".equals(markType)){
				typeName1 = typeBean.getType();
				type1.add(typeBean.getValue());
			}else if("type2".equals(markType)){
				typeName2 = typeBean.getType();
				type2.add(typeBean.getValue());
			}else if("type3".equals(markType)){
				typeName3 = typeBean.getType();
				type3.add(typeBean.getValue());
			}
		}
		markMap = null;
		lstOptions.add(type2Options(typeName1,type1));
		if(!type2.isEmpty()) {
			lstOptions.add(type2Options(typeName2,type2));
		}
		if(!type3.isEmpty()) {
			lstOptions.add(type2Options(typeName3,type3));
		}
		return OptionWrap.builder().lstImages(image).options(lstOptions).build();
    }

    private Options type2Options(String typeName,List<String> lstValue){
		Options options = new Options();
		options.setName(typeName);
		options.setValues(lstValue);
		return options;
	}

    /**我司产品sku转shopify产品Variants
     * @param skuProducts
     * @param typeList
     * @return
     */
    public List<Variants> sku2Variants(String skuProducts, List<TypeBean> typeList, String weight, String weightUnit){
    	if(StringUtils.isBlank(skuProducts)) {
    		return Lists.newArrayList();
    	}
    	Map<String,TypeBean> typeMap = new HashMap<>();
    	for(int i=0,size = typeList.size();i<size;i++) {
    		typeMap.put(typeList.get(i).getId(), typeList.get(i));
    	}
    	List<Variants> lstVariants = Lists.newArrayList();
		try {
			JSONArray skuProductsArray = JSONArray.fromObject(skuProducts);
			Variants variants;
			for(int i=0;i<skuProductsArray.size();i++){
				JSONObject skuProductsObject = JSONObject.fromObject(StrUtils.object2Str(skuProductsArray.get(i)));
				String skuPropIds = skuProductsObject.getString("skuPropIds");
				JSONObject skuValObject = JSONObject.fromObject(StrUtils.object2Str(skuProductsObject.get("skuVal")));
				String actSkuCalPrice = StrUtils.object2Str(skuValObject.get("actSkuCalPrice"));
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						StrUtils.object2Str(skuValObject.get("actSkuPrice")) : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						StrUtils.object2Str(skuValObject.get("actSkuMultiCurrencyDisplayPrice")) : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						StrUtils.object2Str(skuValObject.get("skuMultiCurrencyDisplayPrice")) : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						StrUtils.object2Str(skuValObject.get("skuPrice")) : actSkuCalPrice;

				String[] skuPropIdsSplit = skuPropIds.split(",");
				int length = skuPropIdsSplit.length;
				if(length == 0) {
					continue;
				}
				variants = new Variants();
		        variants.setPrice(actSkuCalPrice);
		        variants.setSku(skuPropIds.replace(",", "_"));
		        variants.setRequires_shipping(true);
		        variants.setWeight(StrUtils.object2Str(skuProductsObject.get("fianlWeight")));
		        variants.setWeight_unit(weightUnit);
		        variants.setCountry_code_of_origin("CN");
		        variants.setInventory_policy("deny");
		        String availQuantity = StrUtils.object2Str(skuValObject.get("availQuantity"));
		        variants.setInventory_quantity(Integer.valueOf(StrUtils.isNum(availQuantity) ? availQuantity : "0"));
		        variants.setInventory_management("shopify");
		        TypeBean typeBean = typeMap.get(skuPropIdsSplit[0]);

		        if(typeBean != null) {
		        	variants.setOption1(typeBean.getValue());
		        }

		        typeBean = length > 1 ? typeMap.get(skuPropIdsSplit[1]) : null;
		        if(typeBean != null) {
		        	variants.setOption2(typeBean.getValue());
		        }

		        typeBean = length > 2 ? typeMap.get(skuPropIdsSplit[2]) : null;
		        if(typeBean != null) {
		        	variants.setOption3(typeBean.getValue());
		        }
		        List<PresentmentPrices> presentment_prices = new ArrayList<>();
		        PresentmentPrices prices = new PresentmentPrices();
		        prices.setCompare_at_price(null);
		        Price price = new Price();
		        price.setAmount(actSkuCalPrice);
		        price.setCurrency_code("USD");
		        prices.setPrice(price);
		        presentment_prices.add(prices);
		        variants.setPresentment_prices(presentment_prices);
		        lstVariants.add(variants);

			}
		} catch (Exception e) {
			log.error("SkuJsonParse->sku2Variants:"+e.getMessage());
		}
    	return lstVariants;
    }

}
