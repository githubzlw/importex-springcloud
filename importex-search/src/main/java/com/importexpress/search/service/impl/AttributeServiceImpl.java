package com.importexpress.search.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.importexpress.search.mapper.AttributeMapper;
import com.importexpress.search.pojo.Attribute;
import com.importexpress.search.pojo.AttributeWrap;
import com.importexpress.search.pojo.SearchParam;
import com.importexpress.search.service.AttributeService;
import com.importexpress.search.service.base.UriService;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttributeServiceImpl extends UriService implements AttributeService {
	@Autowired
	private ServletContext application;

	@Autowired
	private AttributeMapper attributeMapper;

	@Override
	public Map<String, Attribute> getAttributes() {
		Map<String, Attribute> map = Maps.newHashMap();
		List<Attribute> attributes = attributeMapper.getAttributes();
		for(Attribute a : attributes) {
			map.put(a.getId(), a);
		}
		return map;
	}

	@Override
	public AttributeWrap selectedAttributes(SearchParam param) {
		AttributeWrap result = new AttributeWrap();
		String attrId = param.getAttrId();
		if(StringUtils.isBlank(attrId)) {
			result.setAttrs(Lists.newArrayList());
			return null;
		}
		//
		Map<String, Attribute> byPvids = (Map<String, Attribute>)application.getAttribute("newpvidList");

		//初始化
		String url = initUri(param).replaceAll("pvid=.*", "")+"pvid=";

		List<Attribute> filterAttr = Lists.newArrayList();

		String[] attrIds = attrId.split(",");
		attrId = "," + attrId + ",";
		for(String attr : attrIds) {
			Attribute attributeBean = byPvids.get(attr);
			if(attributeBean == null){
				continue;
			}
			String surplusAttr = attrId.replace("," + attr + ",", ",");
			surplusAttr = surplusAttr.startsWith(",") ? surplusAttr.substring(1) : surplusAttr;
			surplusAttr = surplusAttr.endsWith(",") ? surplusAttr.substring(0, surplusAttr.length()-1) : surplusAttr;
			attributeBean.setUrl(StringUtils.isBlank(surplusAttr) ? "" : url + surplusAttr);
			filterAttr.add(attributeBean);
		}
		result.setName("Clear");
		result.setAttrs(filterAttr);
		return result;
	}

	@Override
	public List<AttributeWrap> attributes(SearchParam param, List<FacetField> facetFields) {
		if(facetFields == null) {
			return Lists.newArrayList();
		}
		Map<String, Attribute> byPvids = (Map<String, Attribute>)application.getAttribute("newpvidList");

		//初始化
		String url = initUri(param);

		//解析solr结果到类别
		List<Attribute> attributes = facetAttributes(facetFields, byPvids,param);

		//排序
		List<AttributeWrap> attributeWrap = attributeWrap(attributes, url);

		return attributeWrap;
	}


	/**按照属性重新获取属性列表
	 * @param attributes
	 * @param url
	 * @return
	 */
	private List<AttributeWrap> attributeWrap(List<Attribute> attributes, String url){
		Map<String,AttributeWrap> attrMap = Maps.newHashMap();
		for(Attribute attr : attributes) {
			attr.setUrl(url+attr.getId());
			String id = attr.getId().split("_")[0];
			AttributeWrap wrap = attrMap.get(id);
			wrap = wrap == null ? new AttributeWrap() : wrap;
			List<Attribute> attrList = wrap.getAttrs();
			attrList = attrList == null ? Lists.newArrayList() : attrList;

			attrList.add(attr);
			wrap.setId(id);
			wrap.setName(attr.getName());
			wrap.setAttrs(attrList);
			attrMap.put(id, wrap);
		}
		List<AttributeWrap>  attributeWraps = attrMap.entrySet()
				.stream().filter(m->m.getValue().getAttrs().size()>2).map(m->m.getValue()).collect(Collectors.toList());
		return attributeWraps;
	}


	/**
	 * 统计facet属性
	 * @param facetFields
	 * @param pvids
	 * @return
	 */
	private List<Attribute> facetAttributes(List<FacetField> facetFields,
											Map<String, Attribute> pvids,SearchParam  param){
		List<Attribute> attributes = Lists.newArrayList();
		String attrId = param.getAttrId();
		List<String> lstParamId = Lists.newArrayList();
		if(StringUtils.isNotBlank(attrId)){
			String[] attrIds = attrId.split(",");
			for(String s : attrIds){
				lstParamId.add(s.split("_")[0]);
			}
		}

		for(FacetField facet : facetFields){
			List<Count> values = facet.getValues();
			for(Count value : values){
				String id = value.getName();
				Attribute attr = pvids.get(id);
				if(attr == null || lstParamId.contains(attr.getId().split("_")[0])){
					continue;
				}
				attributes.add(attr);
			}
		}
		return attributes;
	}


	@Override
	public String initUri(SearchParam param) {
		StringBuffer sb_href = new StringBuffer();
		sb_href.append(uriBase(param));

		if(StringUtils.isNotBlank(param.getCatid())) {
			sb_href.append("&catid=").append(param.getCatid());
		}
		if(param.getCollection() != 0){
			sb_href.append("&collection=").append(param.getCollection());
		}
		sb_href.append("&pvid=");
		if(StringUtils.isNotBlank(param.getAttrId())){
			sb_href.append(param.getAttrId()).append(",");
		}

		return sb_href.toString();
	}

}
