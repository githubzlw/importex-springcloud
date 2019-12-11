package com.importexpress.search.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.importexpress.search.mapper.SynonymMapper;
import com.importexpress.search.pojo.SynonymsCategoryWrap;
import com.importexpress.search.service.SynonymService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SynonymServiceImpl implements SynonymService {
    @Autowired
    private SynonymMapper synonymMapper;

    @Override
    public Map<String,Set<String>> getSynonymKeyword() {
        Map<String,Set<String>> synonymsListResult = Maps.newHashMap();
        List<Map<String,String>> lstSynonym = synonymMapper.getSynonymKeyword();
        for(Map<String,String> map : lstSynonym){
            String keyword = map.get("keyword").toLowerCase();
            String[] keyword1s = map.get("keyword1").toLowerCase().split(";");
            for(int i=0;i<keyword1s.length;i++) {
                String k = keyword1s[i];
                if(StringUtils.equals(keyword, k)) {
                    continue;
                }
                Set<String> kSet = synonymsListResult.get(k);
                kSet = kSet == null ? Sets.newHashSet() : kSet;
                kSet.add(keyword);
                synonymsListResult.put(k, kSet);
            }
        }
        return synonymsListResult;
    }

    @Override
    public List<SynonymsCategoryWrap> getSynonymsCategory() {
        List<SynonymsCategoryWrap> catidResult = Lists.newArrayList();
        List<Map<String, String>> specialCatidAll = synonymMapper.getSynonymsCategory();
        if(specialCatidAll == null || specialCatidAll.isEmpty()){
            return catidResult;
        }
        SynonymsCategoryWrap wrap = null;
        for(Map<String,String> map : specialCatidAll){
            String category = map.get("category").toLowerCase().trim();
            if(StringUtils.isNotBlank(category)){
                category = category.replaceAll("(\\s+)"," ");

                wrap = SynonymsCategoryWrap.builder()
                        .category(category).catid(map.get("catid")).length(category.length())
                        .num(category.split("(\\s+)").length).build();

                catidResult.add(wrap);
            }
            String synonyms_category = map.get("synonyms_category");
            if(StringUtils.isNotBlank(synonyms_category)){
                String[] synonymsCategories = synonyms_category.toLowerCase().trim().split(",");
                for(String s : synonymsCategories){
                    s = s.toLowerCase().trim();
                    s = s.replaceAll("(\\s+)"," ");
                    if(StringUtils.isNotBlank(s)){
                        wrap = SynonymsCategoryWrap.builder()
                                .category(s).catid(map.get("catid")).length(s.length())
                                .num(s.split("(\\s+)").length).build();

                        catidResult.add(wrap);
                    }
                }
            }
        }
        List<SynonymsCategoryWrap> result = catidResult.stream()
                .sorted(Comparator.comparing(SynonymsCategoryWrap::getLength).reversed())
                .collect(Collectors.toList());
        return result;
    }
}
