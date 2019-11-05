package com.importexpress.ali1688.control;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.ali1688.service.Ali1688APIUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luohao
 */
@RestController
@Slf4j
public class Ali1688Controller {

    @GetMapping("/getItem")
    public JSONObject getItem(@RequestParam("pid") Long pid){

        return Ali1688APIUtil.getInstance().getItem(pid);
    }

    @GetMapping("/getItems")
    public List<JSONObject> getItems(Long[] pid){
        return Ali1688APIUtil.getInstance().getItems(pid);
    }
}
