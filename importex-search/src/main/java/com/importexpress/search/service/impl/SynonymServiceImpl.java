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

import java.util.*;
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
//        reverSynonyms(synonymsListResult);
        return synonymsListResult;
    }

    @Override
    public List<SynonymsCategoryWrap> getSynonymsCategory() {
        List<String> mul = Lists.newArrayList();
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
                if(mul.contains(category+map.get("catid"))){
                    continue;
                }
                mul.add(category+map.get("catid"));
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
                    if(StringUtils.isNotBlank(s)&& !mul.contains(s+map.get("catid"))){
                        wrap = SynonymsCategoryWrap.builder()
                                .category(s).catid(map.get("catid")).length(s.length())
                                .num(s.split("(\\s+)").length).build();
                        mul.add(s+map.get("catid"));
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
    private String reverKey(String k){
        if(k.indexOf(" ")==-1){
            return "";
        }
        String[] split = k.split("(\\s+)");
        int length = split.length;
        for(int i=length-1;i>0;i--){
            k = k+" "+split[i];
        }
        return k.trim();
    }

    /**
     * 反向同义词
     * @param synonymsListResult
     */
    private void reverSynonyms(Map<String,Set<String>> synonymsListResult){
        Map<String,Set<String>> synonymsListResultRever = Maps.newHashMap();
        Iterator<Map.Entry<String, Set<String>>> iterator = synonymsListResult.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Set<String>> next = iterator.next();
            String key = next.getKey();
            String reverKey = reverKey(key);
            if(StringUtils.isBlank(reverKey)){
                continue;
            }
            Set<String> reverSet = synonymsListResult.get(reverKey);
            if(reverSet == null || reverSet.isEmpty()){
                reverSet = next.getValue();
            }else{
                reverSet.addAll(next.getValue());
            }
            synonymsListResultRever.put(reverKey,reverSet);
        }
        synonymsListResult.putAll(synonymsListResultRever);
    }
}
