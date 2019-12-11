package com.importexpress.search.service.impl;

import com.importexpress.search.mapper.KeywordRecordMapper;
import com.importexpress.search.service.KeywordRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeywordRecordServiceImpl implements KeywordRecordService {

	@Autowired
	private KeywordRecordMapper keywordRecordMapper;

	@Override
	public List<Map<String, String>> getKeywordCatidList() {
		// TODO Auto-generated method stub
		return keywordRecordMapper.getKeywordCatidList();
	}

	@Override
	public List<Map<String, String>> getPriorityCategoryList() {
		return keywordRecordMapper.getPriorityCategoryList();
	}

	@Override
	public List<Map<String, String>> getCategoryPriceList() {
		return keywordRecordMapper.getCategoryPriceList();
	}
	@Override
	public List<Map<String, Object>> getSpecialCategoryList() {
		// TODO Auto-generated method stub
		return keywordRecordMapper.getSpecialCategoryList();
	}
	@Override
	public Map<Integer,List<String>> getSpecialCategory() {
		Map<Integer,List<String>> specialCatidResult = new HashMap<Integer, List<String>>();
		List<Map<String, Object>> specialCatidAll = getSpecialCategoryList();
		for(Map<String,Object> map : specialCatidAll){
			int type = (int)map.get("type");
			List<String> typeList = specialCatidResult.get(type);
			typeList = typeList == null ? new ArrayList<>() : typeList;
			typeList.add((String)map.get("catid"));
			specialCatidResult.put(type, typeList);
		}
		return specialCatidResult;
	}

}
