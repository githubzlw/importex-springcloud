package com.importexpress.shopify.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.shopify.pojo.OptionWrap;
import com.importexpress.shopify.pojo.SkuAttr;
import com.importexpress.shopify.pojo.SkuVal;
import com.importexpress.shopify.pojo.TypeBean;
import com.importexpress.shopify.pojo.product.Options;
import com.importexpress.shopify.pojo.product.PresentmentPrices;
import com.importexpress.shopify.pojo.product.Price;
import com.importexpress.shopify.pojo.product.Variants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


	/**解析sku,转成shopify网站的Options以及Variants
	 * @param typeList
	 * @param skus
	 * @param skuProducts
	 */
	public OptionWrap optionVariant(List<TypeBean> typeList,List<String> skus,String skuProducts) throws Exception {
		if(typeList == null || typeList.isEmpty() || StringUtils.isBlank(skuProducts)){
			return OptionWrap.builder().lstImages(Lists.newArrayList())
					.options(Lists.newArrayList()).variants(Lists.newArrayList()).build();
		}
		skus = skus == null ? Lists.newArrayList() : skus;
		boolean initSku = skus == null || skus.isEmpty();
		List<String> image = Lists.newArrayList();
		List<Variants> lstVariants = Lists.newArrayList();
		List<Options> lstOptions = Lists.newArrayList();

		Map<String,Options> optionMap = Maps.newHashMap();
		Map<String,TypeBean> typeMap = Maps.newHashMap();
		typeList.stream().forEach(t->typeMap.put(t.getId(),t));
		Gson gson = new Gson();
		List<SkuAttr> skuProductsArray = gson.fromJson(skuProducts,
				new TypeToken<List<SkuAttr>>(){}.getType());
		Variants variants;
		for(int i=0;i<skuProductsArray.size();i++){
			SkuAttr skuAttr = skuProductsArray.get(i);
			String skuPropIds = skuAttr.getSkuPropIds();
			if((initSku && skus.size() > 99) || (!initSku && !skus.contains(skuPropIds))){
				continue;
			}

			SkuVal skuValObject = skuAttr.getSkuVal();
			String actSkuCalPrice = skuPrice(skuValObject);
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
			variants.setWeight_unit("kg");
			variants.setCountry_code_of_origin("CN");
			variants.setInventory_policy("deny");
			String availQuantity = StrUtils.object2Str(skuValObject.getAvailQuantity());
			variants.setInventory_quantity(Integer.valueOf(StrUtils.isNum(availQuantity) ? availQuantity : "0"));
			variants.setInventory_management("shopify");

			for(int j=0;j<length;j++){
				TypeBean typeBean = typeMap.get(skuPropIdsSplit[j]);
				options(optionMap,typeBean);
				if(j == 0){
					variants.setOption1(typeBean!=null ? typeBean.getValue() : "");
				}else if(j == 1){
					variants.setOption2(typeBean!=null ? typeBean.getValue() : "");
				}else{
					variants.setOption3(typeBean!=null ? typeBean.getValue() : "");
				}
				String img = typeBean.getImg().replace(".60x60", ".400x400");
				if(StringUtils.isNotBlank(img) && !image.contains(img)){
					image.add(img);
				}
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

			boolean isActive = StringUtils.isNotBlank(variants.getOption1());
			isActive = length > 1 ? isActive && StringUtils.isNotBlank(variants.getOption2()) : isActive;
			isActive = length > 2 ? isActive && StringUtils.isNotBlank(variants.getOption3()) : isActive;

			if(isActive){
				lstVariants.add(variants);
			}
			if(initSku && skus.size() < 101){
				skus.add(skuPropIds);
			}
		}
		optionMap.entrySet().stream().forEach(o->lstOptions.add(o.getValue()));
		return OptionWrap.builder().lstImages(image)
				.options(lstOptions).variants(lstVariants).build();
	}

	/**options转换
	 * @param optionMap
	 * @param typeBean
	 */
	private void options(Map<String,Options>optionMap,TypeBean typeBean){
		Options options = optionMap.get(typeBean.getType());
		options = options == null ? new Options() : options;
		options.setName(typeBean.getType());
		List<String> values = options.getValues();
		values = values == null ? Lists.newArrayList() : values;
		if(!values.contains(typeBean.getValue())){
			values.add(typeBean.getValue());
			values = values.stream().sorted().collect(Collectors.toList());
		}
		options.setValues(values);
		optionMap.put(typeBean.getType(),options);
	}


	/**sku价格
	 * @param skuVal
	 * @return
	 */
	private String skuPrice(SkuVal skuVal){
		String actSkuCalPrice = skuVal.getFreeSkuPrice();
		if(StringUtils.isNotBlank(actSkuCalPrice)){
			return actSkuCalPrice;
		}
		actSkuCalPrice = skuVal.getActSkuCalPrice();
		actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
				skuVal.getActSkuPrice() : actSkuCalPrice;
		actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
				skuVal.getActSkuMultiCurrencyDisplayPrice() : actSkuCalPrice;
		actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
				skuVal.getSkuMultiCurrencyDisplayPrice() : actSkuCalPrice;
		actSkuCalPrice = StringUtils.isBlank(actSkuCalPrice) ?
				skuVal.getSkuPrice() : actSkuCalPrice;
		return actSkuCalPrice;
	}






    /**我司产品规格属性Type转成shopify规格属性Options
     * @param typeList
     * @return
     */
    /*public OptionWrap spec2Options(List<TypeBean> typeList){
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
    }*/

    /*private int arrLength(Map<String,List<String>> map){
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
	}*/

    /**我司产品sku转shopify产品Variants
     * @param skuProducts
     * @param typeList
     * @return
     */
    /*public List<Variants> sku2Variants(String skuProducts, List<Options> options, List<TypeBean> typeList, String weightUnit){
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
    }*/


   /* private boolean setOption(TypeBean typeBean,Variants variants,List<String> lstValue1,
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
	}*/

}
