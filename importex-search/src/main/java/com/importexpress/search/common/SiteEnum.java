package com.importexpress.search.common;

import com.importexpress.search.pojo.CategoryWrap;
import com.importexpress.search.service.SiteOperation;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;

@Slf4j
public enum SiteEnum implements SiteOperation {
    IMPORT(1){
        @Override
        public Map<String, List<CategoryWrap>> dateMap(ServletContext application) {
            return (Map<String, List<CategoryWrap>>)application.getAttribute("importDate");
        }
    },
    KIDS(2){
        @Override
        public Map<String, List<CategoryWrap>> dateMap(ServletContext application) {
            return (Map<String, List<CategoryWrap>>)application.getAttribute("kidsDate");
        }
    },
    PETS(4){
        @Override
        public Map<String, List<CategoryWrap>> dateMap(ServletContext application) {
            return (Map<String, List<CategoryWrap>>)application.getAttribute("petsDate");
        }
    };

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    SiteEnum(int code) {
        this.code = code;
    }
}
