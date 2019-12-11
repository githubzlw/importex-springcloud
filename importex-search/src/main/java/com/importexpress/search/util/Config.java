package com.importexpress.search.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${SOLR.SERVER.URL}")
    public String SOLR_SERVER_URL;


}