package com.importexpress.search.control;

import com.importexpress.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luohao
 */
@RestController
@Slf4j
public class SearchController {

    private SearchService service;

    @Autowired
    public SearchController(SearchService searchService) {
        this.service = searchService;
    }

    @GetMapping("/hello")
    public String hello() {

        return "hello world!";
    }


}
