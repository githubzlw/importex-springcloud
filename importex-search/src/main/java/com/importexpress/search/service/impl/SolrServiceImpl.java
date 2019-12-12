package com.importexpress.search.service.impl;

import com.google.common.collect.Lists;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.common.KeywordCorrect;
import com.importexpress.search.common.SolrOperationUtils;
import com.importexpress.search.common.SwitchDomainUtil;
import com.importexpress.search.pojo.KeyToCategoryWrap;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.pojo.SolrFacet;
import com.importexpress.search.service.SolrService;
import com.importexpress.search.service.base.SolrBase;
import com.importexpress.search.util.Config;
import com.importexpress.search.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SolrServiceImpl extends SolrBase implements SolrService {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private HttpSolrClient httpSolrClient;
    @Autowired
    private SolrOperationUtils solrOperationUtils;

    @Autowired
    public SolrServiceImpl(Config config){
        this.httpSolrClient = new HttpSolrClient.Builder(config.SOLR_SERVER_URL).build();
    }
    @Override
    public QueryResponse groupCategory(SearchParam param) {
        //设置参数
        if(StringUtils.isBlank(param.getKeyword())){
            log.error("param.getKeyword() is null ");
            return null;
        }
        param.setOrder(false);
        ModifiableSolrParams solrParams = getSolrQuery(param);
        if(solrParams == null){
            return null;
        }
        SolrFacet facet = new SolrFacet("custom_path_catid",1,5000);
        setFacet(solrParams,facet);

        setFQ(removeFQ(param,solrParams.get("fq")),solrParams);
        setFL("custom_enname,custom_pid",solrParams);
        setRows(0,1,solrParams);

        //获取请求
        QueryResponse response = sendRequest(solrParams,httpSolrClient);
        //取分组统计列表
        return response;
    }

    @Override
    public QueryResponse serach(SearchParam param) {
        //拼接参数
        ModifiableSolrParams solrParams = getSolrQuery(param);
        if (solrParams == null) {
            return null;
        }
        //setFacte
        setAttributeFacet(solrParams, param);
        //执行查询
        return requestSolr(solrParams);
    }

    @Override
    public QueryResponse shopSerach(SearchParam param) {
        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        //创建查询对象:默认配置
        setQ("*", solrParams);
        setDF("custom_default", solrParams);
        setSort("custom_is_stock_flag desc", solrParams);

        //指定过滤条件
        String specialCatidSearch = solrOperationUtils.specialCatidSearch(param);
        StringBuilder filterQueries = new StringBuilder(specialCatidSearch);
        filterQueries.append("custom_valid:1").append(" AND custom_shop_id:" + param.getStoried());
        //权限版搜索,只展示可搜索的产品
        importType(param, filterQueries);

        setFQ(filterQueries.toString(), solrParams);

        //设置页码参数
        setRows(param, solrParams);

        return sendRequest(solrParams,httpSolrClient);
    }

    @Override
    public QueryResponse bought(SearchParam param) {
        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        String queryString = param.getKeyword();
        StringBuilder q = new StringBuilder();
        if (StringUtils.isNotBlank(queryString) && !"null".equals(queryString)) {
            q.append("(custom_enname:" + queryString + " OR custom_rw_keyword:" + queryString + ")");
        }
        if (StringUtils.isNotBlank(param.getPid())) {
            q.append(q.toString().length() > 0 ? " AND " : "");
            q.append(" -custom_pid:\"" + param.getPid() + "\" ");
        }
        setQ(q.toString().length() > 0 ? q.toString() : "*", solrParams);

        //搜索限定类别
        String specialCatidSearch = solrOperationUtils.specialCatidSearch(param);
        setFQ(specialCatidSearch + " custom_valid:1 AND custom_price:[10 TO *] ", solrParams);

        setRows(0, 12, solrParams);

        int num = (int) (Math.random() * 100000);
        setSort("custom_is_sold_flag desc,rand_" + num + " desc,custom_sold desc,custom_ali_sold desc", solrParams);

        return requestSolr(solrParams);
    }

    @Override
    public QueryResponse catIdForGoods(SearchParam param) {
        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        setRows(0, 12, solrParams);

        int num = (int) (Math.random() * 100000);
        setSort("custom_sold desc,rand_" + num + " desc,custom_ali_sold desc", solrParams);

        StringBuilder q = new StringBuilder();
        if (StringUtils.isNotBlank(param.getCatid()) && StringUtils.isNotBlank(param.getPid())) {
            q.append("custom_path_catid:" + param.getCatid() + " AND -custom_pid:\"" + param.getPid() + "\"");
            if (Utility.homeCatid.length > 0) {
                q.append(" AND ");
            }
        }
        for (int i = 0; i < Utility.homeCatid.length; i++) {
            q.append("-custom_path_catid:\"").append(Utility.homeCatid[i]).append("\" ");
            q.append(" AND ");
        }
        q.append(" custom_valid:1");
        setQ(q.toString(), solrParams);

        StringBuilder fq = new StringBuilder(solrOperationUtils.specialCatidSearch(param));
        fq.append(" custom_price:[2 TO *] AND custom_valid:1");
        setFQ(fq.toString(), solrParams);
        return sendRequest(solrParams, httpSolrClient);
    }

    @Override
    public QueryResponse errorRecommend(SearchParam param) {
        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        setRows(0, 12, solrParams);

        int num = (int) (Math.random() * 100000);
        setSort("rand_" + num + " desc,custom_ali_sold desc,custom_sold desc", solrParams);

        setQ("custom_valid:1", solrParams);

        StringBuilder fq = new StringBuilder(solrOperationUtils.specialCatidSearch(param));
        fq.append(" custom_price:[10 TO *] ");
        if (StringUtils.isNotBlank(param.getCatid())) {
            fq.append(" AND custom_path_catid:" + param.getCatid());
        }
        setFQ(fq.toString(), solrParams);
        return requestSolr(solrParams);
    }

    @Override
    public QueryResponse hotProduct(SearchParam param) {
        ModifiableSolrParams solrParams = getSolrQuery(param);

        if (solrParams == null) {
            return null;
        }
        QueryResponse response = sendRequest(solrParams,httpSolrClient);
        //取商品列表
        if (response != null && response.getResults().size() <= 4) {
            setFQ("custom_price:[1 TO *]", solrParams);
            response = sendRequest(solrParams, httpSolrClient);
        }
        return response;
    }

    @Override
    public QueryResponse hotProductForCatid(SearchParam param) {
        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        setQ("custom_valid:1 AND custom_path_catid:" + param.getCatid() + "", solrParams);
        setSort("custom_is_sold_flag  desc,custom_ali_sold desc,custom_sold desc", solrParams);
        setRows(0, 200, solrParams);
        String specialCatidSearch = solrOperationUtils.specialCatidSearch(param);
        setFQ(specialCatidSearch + " custom_price:[10 TO *]", solrParams);
        QueryResponse response = sendRequest(solrParams, httpSolrClient);
        if (response != null && response.getResults().size() <= 4) {
            setFQ("custom_price:[1 TO *]", solrParams);
            response = sendRequest(solrParams, httpSolrClient);
        }
        return response;
    }

    @Override
    public QueryResponse searPriceRangeByKeyWord(SearchParam param) {
        return null;
    }

    @Override
    public SpellCheckResponse searchAutocomplete(String keyWord,int site) {
        ModifiableSolrParams solrParams =new ModifiableSolrParams();
        setQT("/suggest",solrParams);
        setQ(SwitchDomainUtil.switchAutoKey(keyWord.toLowerCase(),site),solrParams);
        QueryResponse response = sendRequest(solrParams,httpSolrClient);
        if(response == null){
            return null;
        }
        return response.getSpellCheckResponse();
    }

    /**基础请求参数
     * Q、FQ、SORT、ROW、DF、FL
     * @param param
     * @return
     */
    private ModifiableSolrParams getSolrQuery(SearchParam param){
        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        //搜索词过长处理一下
        String queryString = solrOperationUtils.queryString(param.getKeyword());
        boolean isValidQueryString = StringUtils.equals(queryString, "*");
        String fq = null;
        //搜索词替换掉类别
        if(param.getSite() == 1){
            KeyToCategoryWrap keyToCategoryWrap = solrOperationUtils.queryStrToCategory(queryString);
            if(keyToCategoryWrap != null){
                List<String> lstCatid = keyToCategoryWrap.getLstCatid();
                if(lstCatid== null || lstCatid.isEmpty()){
                    return null;
                }else{
                    queryString = solrOperationUtils.queryString(keyToCategoryWrap.getKeyword());
                    isValidQueryString = StringUtils.equals(queryString, "*");
                    //fq限制类别
                    fq = categoryFQ(lstCatid);
                }
            }
        }
        //搜索词
        String qStr = isValidQueryString ? "*" : qStr(queryString,param.getSite());
        setQ(qStr,solrParams);

        //FQ
        String fqStr = fqStr(param);
        //类别同义词设置后
        fq = fq != null? fq + " AND " + fqStr : fqStr;
        setFQ(fq,solrParams);

        //设置查询排序参数
        setSort(sortStr(queryString, param),solrParams);

        //设置rows
        setRows(param,solrParams);

//		String sortStr = scoreFL(param);
        //设置默认搜素域
        setDF("custom_default",solrParams);
        setFL("score,*",solrParams);
        return solrParams;
    }
    /**拼接Q参数
     * @return
     */
    private String qStr(String queryString,int site){
        StringBuilder q_str = new StringBuilder();
        String filterName = solrOperationUtils.queryKey(queryString);
        if(StringUtils.isNotBlank(filterName)){
            q_str.append(" (");
            q_str.append("(").append(filterName.replace("nameQuery:", "custom_enname:"))
                    .append(")^0.9");
            if(site == 1){
                q_str.append(" OR (")
                        .append(filterName.replace("nameQuery:", "custom_type_txt:"))
                        .append(")^1.3)");
            }else{
                q_str.append(" OR (")
                        .append(filterName.replace("nameQuery:", "custom_rw_keyword:"))
                        .append(")^1.3");
                q_str.append(" OR (")
                        .append(filterName.replace("nameQuery:", "custom_category_name:")).append(")^1.1");
                q_str.append(" OR (")
                        .append(filterName.replace("nameQuery:", "custom_keyword:")).append(")^0.9");
            }
            q_str.append(")");
        }
        //产品id搜索
        if(StrUtils.isMatch(queryString, "(\\d+)") && queryString.length() > 5) {
            q_str.append("OR custom_pid:\"").append(queryString).append("\"");
        }
        return q_str.toString();
    }
    /**类别fq
     * @param lstCatid
     * @return
     */
    private String categoryFQ(List<String> lstCatid){
        StringBuilder fq = new StringBuilder("(");
        for(int i=0,length=lstCatid.size();i<length;i++) {
            fq.append(" custom_path_catid:\"").append(lstCatid.get(i)).append("\"");
            if(i != length - 1) {
                fq.append(" OR ");
            }
        }
        fq.append(" )");
        return fq.toString();
    }
    /**solr排序规则
     * @param queryString 查询关键词
     * @param param  搜索参数
     */
    private String sortStr(String queryString, SearchParam param) {
        if(!param.isOrder()) {
            return "";
        }
        StringBuilder sorts = new  StringBuilder();
        //时间排序（暂没有这个排序）
        if(param.getSort().contains("bbPrice")){
            sorts.append("bbPrice-desc".equals(param.getSort())?"custom_price desc":"custom_price asc");
        }else if(param.getSort().equals("order-desc")){
            solrOperationUtils.priorityCategorySort(param.getKeyword(), sorts);
            sorts.append("sum(custom_sold,custom_ali_sold) desc");
        }else{
            solrOperationUtils.priorityCategorySort(param.getKeyword(), sorts);
            sorts.append("product(custom_price,custom_morder")
                    .append(",map(custom_best_match,-1,2,1,0.7)")
                    .append(",map(custom_sold_flag,1,1,0.6,1)")
                    .append(",map(custom_video_flag,1,1,0.7,1)")
                    .append(",map(custom_is_stock_flag,1,1,0.4,1)");
            if(queryString.contains(" ")) {
                sorts.append(",map(termfreq(custom_enname,\""+queryString+"\"),1,10,1,3)");
            }
//			sorts.append(",map(custom_is_sold_flag,2,2,1,3)")
            sorts.append(",map(custom_bm_flag,1,1,0.7,1)")
                    .append(",map(custom_describe_good_flag,1,1,0.7,1)")
                    .append(",map(custom_shop_type,1,1,0.7,1)")
                    .append(",map(custom_quality_avg,0,3,2,1)")
                    .append(",map(custom_weight_sort_flag,1,1,100,1)")
                    .append(",max(0.3,custom_feight_price_rate)")
                    .append(") asc");
            //商品评分
//			sorts.append("custom_score desc,");
//			sorts.append("score desc");
        }
        return sorts.toString();
    }
    /**
     * 设置FQ
     * @param param
     */
    private String fqStr(SearchParam param){
        StringBuilder fq_condition = new StringBuilder();
        //搜索限定类别
        fq_condition.append(solrOperationUtils.specialCatidSearch(param));

        //反关键词
        autiKeyword(param, fq_condition);

        //下架产品不显示
        fq_condition.append("custom_valid:1");

        //new arrival
        if(param.getCollection() == 8){
            String result = formatter.format(LocalDateTime.now().minusDays(60l));
            fq_condition.append(" AND (custom_source_pro_flag:8 OR createtime:[\""+result+"\" TO *])");
        }
        if(StringUtils.isNotBlank(param.getNewArrivalDate()) && !"null".equals(param.getNewArrivalDate())){
            fq_condition.append(" AND custom_new_arrival_date:"+param.getNewArrivalDate()+"");
        }

        //精品区商品
        if(param.isBoutique()){
            fq_condition.append(" AND custom_describe_good_flag:1");
        }

        //价格区间限制
        priceFQ(param, fq_condition);

        //设置规格属性查询
        String pvid = param.getAttrId();
        if(StringUtils.isNotBlank(pvid) ){
            String[] pvids = pvid.split(",");
            for(String tem : pvids){
                fq_condition.append(" AND custom_pvids:\"").append(tem).append("\"");
            }
        }
        //类别限定
        if(StringUtils.isNotBlank(param.getCatid()) && !"0".equals(param.getCatid())) {
            List<String> lstCatid = Lists.newArrayList(Arrays.asList(param.getCatid().split(",")));
            fq_condition.append(" AND ").append(categoryFQ(lstCatid));
        }

        //如果访问链接有限制店铺id
        if(StringUtils.isNotBlank(param.getStoried())){
            fq_condition.append(" AND custom_shop_id:"+param.getStoried()+" ");
        }

        //权限版搜索,只展示可搜索的产品
        importType(param,fq_condition);

        return fq_condition.toString();
    }

    /**权限版搜索,只展示可搜索的产品
     * @param param
     * @param fq_condition
     */
    private void importType(SearchParam param,StringBuilder fq_condition){
        if(param.getSite() == 1){
            fq_condition.append(" AND (");
            //0 默认全部可搜 1-描述很精彩   2-卖过的   3-精选店铺
            if(param.getImportType() == 1){
                fq_condition.append("custom_describe_good_flag:1");
            }else if(param.getImportType() == 2){
                fq_condition.append("custom_sold_flag:1");
            }else if(param.getImportType() == 3){
                fq_condition.append("custom_shop_type:1");
            }else{
                fq_condition.append("custom_searchable:1")
                        .append(" OR ").append("custom_describe_good_flag:1")
                        .append(" OR ").append("custom_sold_flag:1")
                        .append(" OR ").append("custom_shop_type:1");
            }
            fq_condition.append(" )");
        }
    }

    /**价格区间设置
     * @param param
     * @param fq_condition
     */
    private void priceFQ(SearchParam param, StringBuilder fq_condition) {
        String minPrice = param.getMinPrice();
        String maxPrice = param.getMaxPrice();
        String minPrices = solrOperationUtils.categoryPrice(param.getKeyword());
        if(StringUtils.isNotBlank(minPrices)){
            fq_condition.append(" AND custom_price:["+minPrices+" TO *]");
            param.setPrices(minPrices);
        }

        //价格区间设置
        if((StrUtils.isMatch(minPrice, "(\\d+(\\.\\d+){0,1})") && !"0".equals(minPrice) ) ||
                (StrUtils.isMatch(maxPrice, "(\\d+(\\.\\d+){0,1})") && !"0".equals(maxPrice))){
            fq_condition.append(" AND custom_price:[");
            fq_condition.append( StringUtils.isNotBlank(minPrice) ? Double.parseDouble(minPrice)+"":"*");
            fq_condition.append(" TO ");
            fq_condition.append(StringUtils.isNotBlank(maxPrice) ? maxPrice:"*");
            fq_condition.append("] ");
        }
    }
    /**反关键词屏蔽
     * @param param
     * @param fq_condition
     */
    private void autiKeyword(SearchParam param, StringBuilder fq_condition) {
        //判断是否有反关键词查询
        String auti_key = param.getReverseKeywords() ;
        auti_key = solrOperationUtils.reverseKeywords(auti_key,param.getKeyword());
        param.setReverseKeywords(auti_key);

        //有多个反关键词
        if(StringUtils.isNotBlank(auti_key)){
            strArray2FQ(fq_condition, auti_key.split(","));
        }
        //页面反关键词-有多个反关键词
        if(StringUtils.isNotBlank(param.getUnkey())){
            strArray2FQ(fq_condition, param.getUnkey().split(","));
        }
    }
    /**反关键词数组
     * @param fq_condition
     * @param str
     */
    private void strArray2FQ(StringBuilder fq_condition, String[] str) {
        for (String s : str) {
            s = KeywordCorrect.getKeyWord(s);
            if(StringUtils.isBlank(s)) {
                continue;
            }
            s = s.replaceAll("(\\s+)", " +");
            fq_condition.append("-custom_enname:(+"+s+") AND ");
            fq_condition.append("-custom_keyword:(+"+s+") AND ");
            fq_condition.append("-custom_rw_keyword:(+"+s+") AND ");
        }
    }

    /**去掉custom_price:[10 TO *]再次请求
     * @param solrParams
     * @return
     */
    private QueryResponse requestAgain(ModifiableSolrParams solrParams){
        String fq = solrParams.get("fq");
        fq = fq.replace("AND custom_price:[10 TO *]","");
        solrParams.set("fq",fq);
        QueryResponse response = sendRequest(solrParams,httpSolrClient);
        return response;
    }
    /**发起请求
     * @param solrParams
     * @return
     */
    private QueryResponse  requestSolr(ModifiableSolrParams solrParams){
        QueryResponse response = sendRequest(solrParams,httpSolrClient);
        if(response == null){
            return null;
        }
        //取商品列表
        SolrDocumentList documentList = response.getResults();
        String fq = solrParams.get("fq");
        if(StringUtils.isNotBlank(fq) && fq.indexOf("AND custom_price:[10 TO *]") > -1
                && documentList.size() <= 4){
            response = requestAgain(solrParams);
        }
        return response;
    }
    /**擦除FQ
     * @param param
     * @param fq
     * @return
     */
    private String removeFQ(SearchParam param, String fq) {
        if(StringUtils.isNotBlank(param.getCatid()) && !"0".equals(param.getCatid())) {
            fq = fq.replaceAll("( AND \\( custom_path_catid:.* \\))", "");
        }
        if(!param.isMobile()) {
            if(param.getCollection() == 8){
                fq = fq.replaceAll(" AND \\(custom_source_pro_flag\\:8 OR createtime\\:\\[\".*\" TO \\*\\]\\)","");
            }
            if(StringUtils.isNotBlank(param.getNewArrivalDate()) && !"null".equals(param.getNewArrivalDate())){
                fq = fq.replace(" AND custom_new_arrival_date:"+param.getNewArrivalDate(),"");
            }
            fq = fq.replace(" AND custom_is_sold_flag:2","");
        }
        return fq;
    }

    /**
     * 规格属性统计
     *
     * @param solrParams
     * @param param
     */
    private void setAttributeFacet(ModifiableSolrParams solrParams, SearchParam param) {
        String pvid = param.getAttrId();
        if (param.isFactPvid() && pvid.split(",").length < 4) {
            SolrFacet facet = new SolrFacet("custom_pvids", 5000, 4);
            setFacet(solrParams, facet);
        }
    }
}