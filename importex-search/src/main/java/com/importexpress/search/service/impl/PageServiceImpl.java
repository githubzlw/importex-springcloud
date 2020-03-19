package com.importexpress.search.service.impl;

import com.importexpress.search.pojo.PageWrap;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.service.PageService;
import com.importexpress.search.service.base.UriService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class PageServiceImpl extends UriService implements PageService {
	private String aEnd = "</a>&nbsp;&nbsp;";
	private String active = "<span class=\"ui-pagination-active\">";
	private String spanEnd = "</span>&nbsp;&nbsp;";
	private String omit = "<span>....</span>&nbsp;&nbsp;";
	private String pageEnd = "<span class=\"page-end ui-pagination-next ui-pagination-disabled\">next</span>";
	private String preDisable = "<span class=\"ui-pagination-prev ui-pagination-disabled\">pre</span>&nbsp;&nbsp;";

	@Override
	public PageWrap paging(SearchParam param, long recordCount) {
		long rows = param.getPageSize();
		long pageCount = recordCount % rows > 0 ? recordCount / rows + 1 : recordCount / rows;

		PageWrap page = new PageWrap();
		page.setRecordCount(recordCount);
		page.setAmount(pageCount);
		page.setCurrent(param.getPage());
		page.setPageSize(param.getPageSize());

		//分页html
		String pageHtml = pageHtml(param,pageCount,param.getPage());
		page.setPaging(pageHtml);
		return page;
	}
	/**分页html
	 * @param param 搜索参数
	 * @param total 总页数
	 * @param current 当前页
	 * @return
	 */
	private String pageHtml(SearchParam param, long total, long current) {
		//初始化其他参数
		String href = initUri(param);

		//总页数小于8页
		if(total < 8){
			return pageLess(href, total, current);
		}
		//总页数大于7页
		return pageMore(href, total, current);

	}

	/**总页数大于7页
	 * @param href 链接
	 * @param total 总页数
	 * @param current 当前页
	 * @return
	 */
	private String pageMore(String href, long total, long current) {
		//当前页<6, 总页数 > 8
		if(current < 6){
			return pageCurrentLess(href, total, current);
		}

		//当前页>5, 总页数 > 8
		return pageCurrentMore(href, total, current);
	}
	/**当前页>5, 总页数 > 8
	 * @param href 链接
	 * @param total 总页数
	 * @param current 当前页
	 * @return
	 */
	private String pageCurrentMore(String href, long total, long current) {
		StringBuffer sb = new StringBuffer();
		//前一页
		sb.append(prePage(href, current));
		//前3页
		sb.append(middlePage(4L, 1L, current, href));

		//省略页
		sb.append(current - 5 > 0 ? omit : "");

		//当前页相连的三页
		sb.append(middlePage((current+2 > total+1? total+1 : current+2), current-1L, current, href));

		//省略页
		sb.append(current < total - 4 ? omit : "");

		//最后两页（至少是current+2）
		sb.append(lastPage(total+1, total-2, current + 2, href));

		//下一页
		sb.append(nextPage(href, total, current));
		return sb.toString();
	}
	/**当前页<6, 总页数 > 8
	 * @param href 链接
	 * @param total 总页数
	 * @param current 当前页
	 * @return
	 */
	private String pageCurrentLess(String href, long total, long current) {
		StringBuffer sb = new StringBuffer();
		//前一页
		sb.append(prePage(href, current));
		//中间页1-7页
		sb.append(middlePage(8L, 1L, current, href));

		//省略页
		sb.append(total-1 > 8 ? omit : "");

		//最后两页（至少是从第八页开始）
		sb.append(lastPage(total+1, total-1, 8, href));

		//下一页
		sb.append(nextPage(href, total, current));
		return sb.toString();
	}

	/**最后页码
	 * @param num 长度
	 * @param start 开始
	 * @param limit 截止
	 * @param href 链接
	 * @return
	 */
	private StringBuffer lastPage(long num,long start,long limit,String href) {
		StringBuffer sb = new StringBuffer();
		for(long i=(start > limit? start : limit);i<num;i++){
			sb.append("<a href=\"").append(href).append(i).append("\">").append(i).append(aEnd);
		}
		return sb;
	}

	/**总页数小于8
	 * @param href 链接
	 * @param total 总页数
	 * @param current 当前页
	 * @return
	 */
	private String pageLess(String href, long total, long current) {
		StringBuffer sb = new StringBuffer();

		//前一页
		sb.append(prePage(href, current));

		//中间页
		sb.append(middlePage(total+1, 1L, current, href));

		//下一页
		sb.append(nextPage(href, total, current));
		return sb.toString();
	}

	/**
	 * 中间页
	 * @param num 长度
	 * @param start 开始
	 * @param current 当前页
	 * @param href 链接
	 * @return
	 */
	private StringBuffer middlePage(long num,long start,long current,String href ) {

		StringBuffer sb = new StringBuffer();
		for(long i=start;i<num;i++){
			if(i == current){
				sb.append(active).append(i).append(spanEnd);
			}else{
				sb.append("<a href=\"").append(href).append(i).append("\">").append(i).append(aEnd);
			}
		}
		return sb;
	}

	/**前一页
	 * @param href 链接
	 * @param current 当前页
	 * @return
	 */
	private String prePage(String href, long current) {
		if(current == 1){
			return preDisable;
		}
		StringBuffer sb = new StringBuffer("<a class=\"ui-pagination-prev\" href=\"");
		sb.append(href).append(current-1).append("\">pre").append(aEnd);
		return sb.toString();
	}

	/**下一页
	 * @param href 链接
	 * @param current 当前页
	 * @return
	 */
	private String nextPage(String href, long total, long current) {
		if(current == total){
			return pageEnd;
		}
		StringBuffer sb = new StringBuffer("<a class=\"page-next ui-pagination-next\" href=\"");
		sb.append(href).append(current+1).append("\">next</a>");
		return sb.toString();
	}


	@Override
	public String initUri(SearchParam param) {
		StringBuffer sb_href = new StringBuffer();

		if(StringUtils.isNotBlank(param.getUriRequest())){
			sb_href.append("/").append(param.getUriRequest()).append("?");
		}
		sb_href.append(uriBase(param));
		if(org.apache.commons.lang.StringUtils.isNotBlank(param.getCatid())){
			sb_href.append("&catid=").append(param.getCatid());
		}
		if(org.apache.commons.lang.StringUtils.isNotBlank(param.getAttrId())){
			sb_href.append("&pvid=").append(param.getAttrId());
		}
		if(param.getCollection() != 0){
			sb_href.append("&collection=").append(param.getCollection());
		}
        if(StringUtils.isNotBlank(param.getNewArrivalDate())){
            sb_href.append("&newArrivalDate=").append(param.getNewArrivalDate());
        }
		sb_href.append("&page=");
		return sb_href.toString();
	}
}
