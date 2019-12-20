package com.importexpress.search.service;

import com.importexpress.search.pojo.LimitKey;

import java.util.List;

public interface LandingPageTriggerKeyService {
    /**获取触发词
     * @param key
     * @return
     */
    LimitKey getTriggerKey(String key, String adgroupid);

    /**
     * 重新获取数据
     */
    List<LimitKey> refleshTriggerKey();
}
