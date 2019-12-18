package com.importexpress.search.common;

import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.pojo.Currency;
import com.importexpress.search.pojo.SearchParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 搜索参数处理
 */
@Component
@Slf4j
public class VerifySearchParameter {
    public static boolean initApplication = false;
    @Autowired
    private InitApplicationParameter applicationParameter;

    public void initApplication(HttpServletRequest request){
        if(!initApplication){
            applicationParameter.init(request.getServletContext());
            initApplication = true;
        }
    }

    public SearchParam mustPatameter(HttpServletRequest request,SearchParam param){
        initApplication(request);
        checkUserInfo(param);
        freeShipping(param);
        page(param);
        sort(param);
        vexKeyWord(param);
        //店铺id
        storeId(param);
        setRequestUri(param);
        return param;
    }
    /**
     * 搜索请求参数校验
     * @param request
     * @return
     */
    public SearchParam verification(HttpServletRequest request,SearchParam param){
        try {
            mustPatameter(request,param);
            price(param);
            catid(param);
            pvid(param);
            //其他
            otherParameter(param);
            //2019-10-11-精品区商品
            boutique(param);
            //价格参数
//            priceCurrency(param);
        }catch (Exception e){
            log.error("verificate search parameter error",e);
        }
        return param;
    }

    /**
     * 搜索请求参数校验
     * @param request
     * @return
     */
    public SearchParam shopParam(HttpServletRequest request,SearchParam param){
        try {
            mustPatameter(request,param);
            setFalse(param);
        }catch (Exception e){
            log.error("verificate search parameter error",e);
        }
        return param;
    }

    private void setRequestUri(SearchParam param){
        String requestUri = param.getUriRequest();
        requestUri = StringUtils.isBlank(requestUri) ? "/goodslist" : requestUri;
        param.setUriRequest(requestUri);
    }

    /**
     * @param param
     */
    public void setFalse(SearchParam param){
        param.setFactCategory(false);
        param.setFactPvid(false);
        param.setBoutique(false);
        param.setOrder(false);
    }

    /**预处理关键词+类别
     * @date 2016年5月10日
     * @author abc
     */
    public void vexKeyWord( SearchParam param) {
        //获取页面传过来的反关键词
        String filterKey = param.getFKey();
        filterKey = KeywordCorrect.getKeyWord(filterKey);
        param.setFKey(filterKey);

        //search whithin;

        String keyword = param.getKeyword();
        keyword = KeywordCorrect.getKeyWord(keyword);

        //关键词为空  全类搜索
        keyword = StringUtils.isBlank(keyword) ? "*" : keyword;
        param.setKeyword(keyword);

    }
    /**精品区
     * @param param
     */
    private void boutique(SearchParam param){
        //精品区(不统计类别属性)
        boolean boutique = param.isBoutique();
        if(boutique){
            param.setFactCategory( false);
            param.setFactPvid(false);
            param.setBoutique(true);
        }
    }
    /**其他参数
     * @param param
     */
    private void otherParameter( SearchParam param){

        String newArrivalDate = param.getNewArrivalDate();
        newArrivalDate = StrUtils.isMatch(newArrivalDate, "(\\d{4}(\\-\\d{1,2}){1,2})") ? newArrivalDate : null;
        param.setNewArrivalDate(newArrivalDate);
        freeShipping(param);
    }
    /**其他参数
     * @param param
     */
    private void freeShipping(SearchParam param){

        //注册版搜索产品 0 默认全部可搜  1-描述很精彩   2-卖过的   3-精选店铺
        int type = param.getImportType();
        param.setImportType(type< 5 && type > -1 ? type : 0);

        //jxw 2018/07/12 end
        //是否免邮
        int isFreeShip = param.getFreeShipping();
        isFreeShip = isFreeShip == 2 ? 2 : 0;
        param.setFreeShipping(isFreeShip);
    }

    /**访问链接中是否有店铺id
     * @param param
     */
    private void storeId(SearchParam param){
        String storied = param.getStoried();
        storied = StringUtils.isNotBlank(storied) ? storied : "";
        param.setStoried(storied);
    }

    /**用户相关信息校验
     * @param param
     */
    private void checkUserInfo(SearchParam param){
        checkCurrency(param);
        checkUserType(param);
    }

    /**排序：默认default
     * @param param
     */
    private void sort( SearchParam param){
        //搜索排序-默认按照销量排序
        String sort = param.getSort();
        sort = StringUtils.isBlank(sort) ? "default" : sort;
        switch (sort){
            case "order-desc":
              break;
            case "bbPrice-asc":
              break;
            case "createtime":
              break;
              default:
                  sort = "default";
        }
        param.setSort(sort);
    }

    /**价格
     * @param param
     */
    private void price( SearchParam param){
        //最小价格
        String price1 = param.getMinPrice();
        param.setMinPrice(priceStr2Num(price1));
        //最大价格
        String price2 = param.getMaxPrice();
        param.setMaxPrice(priceStr2Num(price2));
    }
    /**字符串中匹配出价格数字字符串
     * @param str
     */
    private String priceStr2Num(String str){
        //最小价格
        str = StrUtils.matchDotNum(str);
        str = str.length() > 5 ? str.substring(0, 5) : str;
        str = str.length() > 0 && Double.valueOf(str) < 0.01 ? "" : str;
        return str;
    }

    /**类别
     * @param param
     */
    public void catid( SearchParam param){
        //类别catid
        String catid = param.getCatid();
        boolean isMoreCatid =  StrUtils.isMatch(catid, "(\\d{1,21}(,\\d{1,21})+)");
        if(!isMoreCatid) {
            catid =  StrUtils.isMatch(catid, "(\\d{1,21})") ?  catid : null;
        }
        param.setCatid("0".equals(catid) ? null:catid);
        //多类别搜索，不做类别、属性统计
        param.setFactCategory( !isMoreCatid);
        param.setFactPvid(!isMoreCatid);
    }
    /**属性
     * @param param
     */
    private void pvid(SearchParam param){
        String pvid = param.getAttrId();
        pvid = StrUtils.isMatch(pvid, "(\\d+_\\d+(,\\d+_\\d+)*)") ? pvid : "";
        pvid = StringUtils.isBlank(pvid) || "0".equals(pvid)? "" : pvid;
        param.setAttrId(pvid);
    }

    /**页码
     * @param param
     */
    private void page( SearchParam param){
        param.setPage(Math.max(param.getPage(),1));
        if(param.isMobile()){
            param.setPageSize(30);
        }
    }

    /**检查用户是否有权限搜索
     * 默认授权  1-授权  0-未授权
     * @param param
     * @return
     */
    public SearchParam checkUserType(SearchParam param){
        int site = param.getSite();
        site = (site & -site) == site ? site : 2;
        param.setSite(site);

        int userType = param.getUserType();
        userType = userType == 1 || site != 1 ? 1 : 0;
        param.setUserType(userType);
        return param;
    }

    /**获取用户货币
     * @param param
     * @return
     */
    public SearchParam checkCurrency(SearchParam param){
        Currency currency = param.getCurrency();
        String strCurrency ;
        String strSymbol;
        double exchangeRate;
        if(currency == null){
            strCurrency = "USD";
            strSymbol = "$";
            exchangeRate = 1.0;
        }else{
            strCurrency = currency.getCurrency();
            strSymbol = currency.getSymbol();
            exchangeRate = currency.getExchangeRate();
        }
        currency = new Currency();
        currency.setCurrency(strCurrency);
        currency.setExchangeRate(exchangeRate);
        currency.setSymbol(strSymbol);
        param.setCurrency(currency);
        return param;
    }

    /**价格区间切换为美元货币
     * @param param
     */
    private static void  priceCurrency(SearchParam param){
        if(StringUtils.isBlank(param.getMinPrice()) && StringUtils.isBlank(param.getMaxPrice())){
            return ;
        }
        Currency currency = param.getCurrency();
        //切换后货币

        if(!"USD".equals(currency.getCurrency())){
            String minPrice = param.getMinPrice();
            minPrice = ChangeCurrency.priceToUSD(minPrice,currency.getExchangeRate());
            param.setMinPrice(minPrice);

            String maxPrice = param.getMaxPrice();
            maxPrice = ChangeCurrency.priceToUSD(maxPrice,currency.getExchangeRate());
            param.setMaxPrice(maxPrice);
        }
    }


}
