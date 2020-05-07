package com.importexpress.search.service.base;

import com.importexpress.search.common.ChangeCurrency;
import com.importexpress.search.common.KeywordCorrect;
import com.importexpress.search.pojo.Currency;
import com.importexpress.search.pojo.SearchParam;
import org.apache.commons.lang3.StringUtils;

public abstract class UriService {
	/**初始化参数
	 * @param param
	 * @return
	 */
	public abstract String initUri(SearchParam param);

	/**初始链接
	 * @param param
	 * @return
	 */
	public String uriBase(SearchParam param) {
		StringBuffer sb_href = new StringBuffer();
		/*if(StringUtils.isBlank(param.getUriRequest())){
			param.setUriRequest("");
		}*/
		sb_href.append("keyword=")
				.append(KeywordCorrect.getKeyWord(param.getKeyword()))
				.append("&srt=")
				.append(StringUtils.isNotBlank(param.getSort()) ? param.getSort() : "default");
		if(param.getImportType() != 0){
			sb_href.append("&filter=").append(param.getImportType());
		}
		if(StringUtils.isNotBlank(param.getMinPrice())){
			String price = priceCurrency(param.getMinPrice(),param.getCurrency());

			sb_href.append("&price1=").append(price);
		}
		if(StringUtils.isNotBlank(param.getMaxPrice())){
			String price = priceCurrency(param.getMaxPrice(),param.getCurrency());
			sb_href.append("&price2=").append(price);
		}
		sb_href.append("&isFreeShip="+param.getFreeShipping());
		return sb_href.toString();
	}
	/**价格区间美元切换为用户选择的货币
	 * @param price
	 * @param currency
	 */
	private static String  priceCurrency(String price,Currency currency){
		//切换后货币
		if(!"USD".equals(currency.getCurrency())){
			price = ChangeCurrency.priceFromUSD(price,currency.getExchangeRate());
		}
		return price;
	}

}
