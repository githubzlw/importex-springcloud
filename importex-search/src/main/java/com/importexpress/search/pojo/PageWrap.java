package com.importexpress.search.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 页码
 * @author sj
 *
 */
@Data
public class PageWrap implements Serializable {
	private static final long serialVersionUID = 986818736352041314L;

	/**
	 * 总数量
	 */
	private long recordCount;
	/**
	 * 每页数量
	 */
	private long pageSize;
	/**
	 * 总页数
	 */
	private long amount;
	/**
	 * 当前页数
	 */
	private long current;
	/**
	 * 分页html
	 */
	private String paging;

}
