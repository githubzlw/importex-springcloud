package com.importexpress.search.service;

import com.importexpress.search.pojo.LimitKey;

public interface LandingPageTriggerKeyService {
    /**获取触发词
     * @param key
     * @return
     */
    LimitKey getTriggerKey(String key, String adgroupid);

}
