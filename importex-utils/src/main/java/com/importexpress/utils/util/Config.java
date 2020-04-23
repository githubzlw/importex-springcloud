package com.importexpress.utils.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${GEO_FILE_PATH}")
    public String GEO_FILE_PATH;

}