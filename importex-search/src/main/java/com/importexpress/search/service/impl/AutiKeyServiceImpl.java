package com.importexpress.search.service.impl;

import com.google.common.collect.Maps;
import com.importexpress.search.mapper.AutiKeyMapper;
import com.importexpress.search.service.AutiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AutiKeyServiceImpl implements AutiKeyService {
    @Autowired
    private AutiKeyMapper autiKeyMapper;

    @Override
    public Map<String, String> getAutiKey() {
        Map<String,String> autiKeyResult = Maps.newHashMap();
        List<Map<String, String>> autiKeyAll = autiKeyMapper.getAutiKeyList();
        for(Map<String,String> map : autiKeyAll){
            autiKeyResult.put(map.get("keyword"), map.get("auti_word"));
        }
        return autiKeyResult;
    }
}
