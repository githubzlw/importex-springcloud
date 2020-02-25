package com.importexpress.shopify.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.util.*;

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

    	Map<String,List<String>> typeMap = Maps.newHashMap();

		//规格数据
		List<String> image = Lists.newArrayList();
		for(TypeBean typeBean : typeList){
			String type = typeBean.getType();
			type = type.endsWith(":") ? type.substring(0, type.length() - 1) : type;
			typeBean.setType(type);

			if(StringUtils.isNotBlank(typeBean.getImg())){
				image.add(typeBean.getImg().replace(".60x60", ".400x400"));
			}

			List<String> lstValue = typeMap.get(type);
			lstValue = lstValue == null ? Lists.newArrayList() : lstValue;
			if(lstValue.contains(typeBean.getValue()) || arrLength(typeMap)> 99){
				continue;
			}
			lstValue.add(typeBean.getValue());
			typeMap.put(type,lstValue);
		}
		typeMap.entrySet().stream().forEach(t->lstOptions.add(type2Options(t.getKey(),t.getValue())));
		return OptionWrap.builder().lstImages(image).options(lstOptions).build();
    }

    private int arrLength(Map<String,List<String>> map){
    	int size= 1;
		Iterator<Map.Entry<String, List<String>>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()){
			size = size * iterator.next().getValue().size();
		}
		return size;
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
    public List<Variants> sku2Variants(String skuProducts, List<Options> options, List<TypeBean> typeList, String weightUnit){
    	if(StringUtils.isBlank(skuProducts) || options == null || options.isEmpty() || typeList == null || typeList.isEmpty()) {
    		return Lists.newArrayList();
    	}
		Options option1 = options.get(0);
		Options option2 = options.size() > 1 ? options.get(1) : null;
		Options option3 = options.size() > 2 ? options.get(2) : null;

		List<String> lstValue1 = option1.getValues();
		List<String> lstValue2 = option2==null?Lists.newArrayList():option2.getValues();
		List<String> lstValue3 = option3==null?Lists.newArrayList():option3.getValues();

    	Map<String,TypeBean> typeMap = new HashMap<>();
    	for(int i=0,size = typeList.size();i<size;i++) {
    		typeMap.put(typeList.get(i).getId(), typeList.get(i));
    	}
    	List<Variants> lstVariants = Lists.newArrayList();
		Gson gson = new Gson();
		try {
//			JSONArray skuProductsArray = JSONArray.fromObject(skuProducts);
			List<SkuAttr> skuProductsArray = gson.fromJson(skuProducts,
					new TypeToken<List<SkuAttr>>() {}.getType());
			Variants variants;
			for(int i=0;i<skuProductsArray.size();i++){
				SkuAttr skuAttr = skuProductsArray.get(i);
//				JSONObject skuProductsObject = JSONObject.fromObject(StrUtils.object2Str(skuProductsArray.get(i)));
				String skuPropIds = skuAttr.getSkuPropIds();
				SkuVal skuValObject = skuAttr.getSkuVal();
//				JSONObject skuValObject = JSONObject.fromObject(StrUtils.object2Str(skuProductsObject.get("skuVal")));
				String actSkuCalPrice = skuValObject.getActSkuCalPrice();//StrUtils.object2Str(skuValObject.get("actSkuCalPrice"));
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						skuValObject.getActSkuPrice() : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						skuValObject.getActSkuMultiCurrencyDisplayPrice() : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						skuValObject.getSkuMultiCurrencyDisplayPrice() : actSkuCalPrice;
				actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
						skuValObject.getSkuPrice() : actSkuCalPrice;

				String[] skuPropIdsSplit = skuPropIds.split(",");
				int length = skuPropIdsSplit.length;
				if(length == 0) {
					continue;
				}
				variants = new Variants();
		        variants.setPrice(actSkuCalPrice);
		        variants.setSku(skuPropIds.replace(",", "_"));
		        variants.setRequires_shipping(true);
		        variants.setWeight(StrUtils.object2Str(skuAttr.getFianlWeight()));
		        variants.setWeight_unit(weightUnit);
		        variants.setCountry_code_of_origin("CN");
		        variants.setInventory_policy("deny");
		        String availQuantity = StrUtils.object2Str(skuValObject.getAvailQuantity());
		        variants.setInventory_quantity(Integer.valueOf(StrUtils.isNum(availQuantity) ? availQuantity : "0"));
		        variants.setInventory_management("shopify");

		        TypeBean typeBean = typeMap.get(skuPropIdsSplit[0]);
				setOption(typeBean,variants,lstValue1,lstValue2,lstValue3);

		        typeBean = length > 1 ? typeMap.get(skuPropIdsSplit[1]) : null;
				setOption(typeBean,variants,lstValue1,lstValue2,lstValue3);

		        typeBean = length > 2 ? typeMap.get(skuPropIdsSplit[2]) : null;
				setOption(typeBean,variants,lstValue1,lstValue2,lstValue3);

				List<PresentmentPrices> presentment_prices = new ArrayList<>();
		        PresentmentPrices prices = new PresentmentPrices();
		        prices.setCompare_at_price(null);
		        Price price = new Price();
		        price.setAmount(actSkuCalPrice);
		        price.setCurrency_code("USD");
		        prices.setPrice(price);
		        presentment_prices.add(prices);
		        variants.setPresentment_prices(presentment_prices);

				boolean isActive = StringUtils.isNotBlank(variants.getOption1());
				isActive = length > 1 ? isActive && StringUtils.isNotBlank(variants.getOption2()) : isActive;
				isActive = length > 2 ? isActive && StringUtils.isNotBlank(variants.getOption3()) : isActive;

		        if(isActive){
					lstVariants.add(variants);
				}

			}
		} catch (Exception e) {
			log.error("SkuJsonParse->sku2Variants:"+e.getMessage());
		}
    	return lstVariants;
    }


    private boolean setOption(TypeBean typeBean,Variants variants,List<String> lstValue1,
						   List<String> lstValue2,List<String> lstValue3){
		if(typeBean == null) {
			return false;
		}

		if(lstValue1.contains(typeBean.getValue())){
			variants.setOption1(typeBean.getValue());
		}else if(lstValue2.contains(typeBean.getValue())){
			variants.setOption2(typeBean.getValue());
		}else if(lstValue3.contains(typeBean.getValue())){
			variants.setOption3(typeBean.getValue());
		}else{
			return false;
		}
		return true;
	}

}
