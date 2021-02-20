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
		Map<Integer, List<String>> specialCatidResult = new HashMap<Integer, List<String>>();
		List<Map<String, Object>> specialCatidAll = getSpecialCategoryList();
		specialCatidAll.addAll(getCategoriesBySite());
		for (Map<String, Object> map : specialCatidAll) {
			int type = (int) map.get("type");
			List<String> typeList = specialCatidResult.get(type);
			typeList = typeList == null ? new ArrayList<>() : typeList;
			typeList.add((String) map.get("catid"));
			specialCatidResult.put(type, typeList);
		}
		return specialCatidResult;
	}

	private List<Map<String, Object>> getCategoriesBySite() {
		List<Map<String, Object>> specialCatidAll = new ArrayList<Map<String, Object>>();
		// pet
		Map<String, Object> map = new HashMap<>();
		map.put("catid", "9210044");
		map.put("type", 4);
		specialCatidAll.add(map);
		map = new HashMap<>();
		map.put("catid", "121776006");
		map.put("type", 4);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410069");
		map.put("type", 2);
		specialCatidAll.add(map);
		map = new HashMap<>();
		map.put("catid", "9410070");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410071");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410072");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410073");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410074");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410075");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410076");
		map.put("type", 2);
		specialCatidAll.add(map);


		map = new HashMap<>();
		map.put("catid", "9410077");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410078");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410079");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410080");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410081");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410082");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410083");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410094");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410095");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410096");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410097");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9310121");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410117");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410120");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410114");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410126");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410118");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410119");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9410116");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9210054");
		map.put("type", 2);
		specialCatidAll.add(map);

		map = new HashMap<>();
		map.put("catid", "9210053");
		map.put("type", 2);
		specialCatidAll.add(map);

		return specialCatidAll;
	}


}
