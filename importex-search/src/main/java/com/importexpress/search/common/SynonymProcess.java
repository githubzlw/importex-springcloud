package com.importexpress.search.common;

import com.google.common.collect.Maps;
import com.importexpress.search.util.ExhaustUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.*;

/**
 * 同义词处理类
 * @author Administrator
 *
 */
@Component
public class SynonymProcess {
	@Autowired
	private ServletContext application;
	@Autowired
    private ExhaustUtils exhaustUtils;
	/**多个搜索词------热卖搜索
	 * @param queryString 以,分割
	 * @return
	 */
	public  String moreKeys(String queryString) {
		//多个搜索词------热卖搜索
		String[] querys  = queryString.split(",");
		if(querys.length < 2) {
			return null;
		}
		StringBuilder q = new StringBuilder();
		for(int i=0,length=querys.length;i<length;i++) {
			if(StringUtils.isBlank(querys[i].trim())) {
				continue;
			}
			if(q.length() > 0 && i != length) {
				q.append(" OR ");
			}
			q.append("nameQuery:(+").append(querys[i].trim().replaceAll("(\\s+)", " +")).append(")");
		}
		return q.toString();
	}
	/**
	 * 获取同义词，配置q参数
	 * @param queryString
	 * @return
	 */
	public  String synonymKey( String queryString) {
		//后台同义词配置搜索
		Object synonymsList = application.getAttribute("synonymsList");
		if(synonymsList == null) {
			queryString = queryString.replaceAll("(\\s+)", " +");
			return "nameQuery:(+"+queryString+")";
		}
		//单个搜索词
		String synonymsString = queryString;
		Map<String, Set<String>> synonyms_map=(HashMap<String,Set<String>>)synonymsList;

		//先整体匹配
		Set<String> kSet = synonyms_map.get(queryString);
		if(kSet == null || kSet.isEmpty()) {
			Map<String, Object> synonySet = synonySet(queryString, synonyms_map);
			kSet = (Set<String>)synonySet.get("kSet");
			synonymsString = (String)synonySet.get("synonymsString");
		}

		if(kSet == null) {
			return "nameQuery:(+"+queryString.replaceAll("(\\s+)", " +")+")";
		}
		//部分有同义词
		String q_str = synonymsMore(queryString, synonymsString, kSet);
		return q_str;
	}

	/**
	 * 部分搜索词有同义词
	 * @param queryString
	 * @param synonymsString
	 * @param kSet
	 * @return
	 */
	private  String synonymsMore(String queryString, String synonymsString, Set<String> kSet) {
		StringBuilder q_str = new StringBuilder();
		StringBuilder synonymsBuilder = new StringBuilder();
		int synonymsKeyDex = 0;
		//去掉同义词后剩余的其他单词
		String otherString = queryString.replace(synonymsString, "").trim().replaceAll("(\\s+)", " +");
		Iterator<String> kSetIterator = kSet.iterator();
		while (kSetIterator.hasNext()) {
			String synonymsKey = kSetIterator.next();
			if(StringUtils.isBlank(synonymsKey)) {
				continue;
			}
			synonymsKey = synonymsKey.trim();
			if(StringUtils.equals(synonymsKey, synonymsString)) {
				continue;
			}
			if(synonymsKeyDex > 0) {
				synonymsBuilder.append(" ");
			}
			synonymsBuilder.append("(+")
			.append(synonymsKey.replaceAll("(\\s+)", " +"));
			if(StringUtils.isNotBlank(otherString)) {
				synonymsBuilder.append(" +").append(otherString);
			}
			synonymsBuilder.append(")");
			synonymsKeyDex++;
		}

		queryString = queryString.replaceAll("(\\s+)", " +");
		if(synonymsKeyDex == 0) {
			q_str.append("nameQuery:(+").append(queryString).append(")");

		}else if(synonymsKeyDex == 1) {
			q_str.append("nameQuery:(")
			.append("(").append(synonymsBuilder).append(")^1 ")
			.append("(+").append(queryString).append(")^0.9");
			q_str.append(")");
		}else {
			q_str.append("nameQuery:(").append("(+").append(queryString).append(")^1.2 ")
			.append("(").append(synonymsBuilder).append(")^1.1").append(")");
		}
		return q_str.toString();
	}


	/**
	 * 匹配查找同义词集合
	 * @param queryString
	 * @param synonyms_map
	 * @return
	 */
	private  Map<String,Object> synonySet(String queryString,Map<String, Set<String>> synonyms_map){
		String synonymsString = queryString;
		Set<String> kSet = null;
		//将搜索词重组同义词词组
		List<String> synonyKeys = exhaustUtils.exhaust(queryString);
		for(String s : synonyKeys){
			synonymsString = s;
			kSet = synonyms_map.get(s);
			if(kSet != null && !kSet.isEmpty()) {
				break;
			}
		}
		Map<String,Object> result = Maps.newHashMap();
		result.put("synonymsString", synonymsString);
		result.put("kSet", kSet);
		return result;
	}


}
