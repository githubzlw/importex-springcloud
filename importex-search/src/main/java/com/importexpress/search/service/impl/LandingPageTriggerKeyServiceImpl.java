package com.importexpress.search.service.impl;

import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.mapper.LandingPageTriggerKeyMapper;
import com.importexpress.search.pojo.LimitKey;
import com.importexpress.search.service.LandingPageTriggerKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import java.util.List;

@Service
public class LandingPageTriggerKeyServiceImpl  implements LandingPageTriggerKeyService {
    @Autowired
    private LandingPageTriggerKeyMapper landingPageTriggerKeyMapper;

    private List<LimitKey> triggerKeyList;
    @Override
    public LimitKey getTriggerKey(String key,String adgroupid) {
        if(triggerKeyList == null) {
            triggerKeyList = landingPageTriggerKeyMapper.getTriggerKeyList();
        }
        Assert.isTrue(triggerKeyList!=null, "trigger key list is null");
        LimitKey result = null;
        for(int i=0,length=triggerKeyList.size();i<length;i++) {
            LimitKey t = triggerKeyList.get(i);
            String adKey = t.getKey().trim().replaceAll("\\s+", "\\\\s+.*");
            if(StrUtils.isFind(key,"\\b("+adKey+")\\b") && adgroupid.equals(t.getAdgroupid())) {
                result = t;
                break;
            }
        }
        return result;
    }
}
