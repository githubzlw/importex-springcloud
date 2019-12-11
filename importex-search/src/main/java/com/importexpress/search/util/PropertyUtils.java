package com.importexpress.search.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author lhao
 * @date 2018/4/20
 */
public final class PropertyUtils {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    private static final String RESOURCE_FILE = "properties/resource.properties";
    private static final String SHIELD_FILE = "properties/shield.properties";
    private static final String MQ_FILE = "properties/mq.properties";
    private static final String CATEGORY_FILE = "properties/category.properties";
    private static String delGegex ;
    /**
     * 缓存配置文件里的值集合
     */
    private static  List<Map<String, String>> lstValue = new CopyOnWriteArrayList<>();

    /**
     * 缓存配置文件里的值集合
     */
    private static  List<String> shieldListValue = new CopyOnWriteArrayList<>();
    private static  List<Map<String,String>> categoryReplaceListValue = null;//new CopyOnWriteArrayList<>();
    private static  List<Map<String,String>> categoryAddListValue = null;//new CopyOnWriteArrayList<>();

    /**
     * 需要过滤的alt,title的id
     */
    private static  String[] pidLists =null;

    /**
     * 类别id组
     */
    private static  String[] catidsList =null;

    /*
     * 缓存配置文件的值
     */
    static {
        lstValue.add(getStringStringHashMap(PropertyFileEnum.RESOURCE_PROPERTIES));
        lstValue.add(getStringStringHashMap(PropertyFileEnum.MQ_PROPERTIES));

        //pidLists = StringUtils.split(getValueFromResourceFile("pidList"), ",");
    }

    private PropertyUtils() {
        //禁止实例化
    }

    private static Map<String, String> getStringStringHashMap(PropertyFileEnum propertyFileEnum) {

        Properties propertiesResource = null;

        try {
            switch (propertyFileEnum) {
                case RESOURCE_PROPERTIES:
                    propertiesResource = PropertiesLoaderUtils.loadAllProperties(RESOURCE_FILE);
                    break;
                case MQ_PROPERTIES:
                    propertiesResource = PropertiesLoaderUtils.loadAllProperties(MQ_FILE);
                    break;
                default:
                    throw new IllegalStateException("no support type");
            }
        } catch (IOException ioe) {
            logger.error("！！！！！！！！！！！！！！读取配置文件失败", ioe);
        }
        Assert.notNull(propertiesResource);
        Map<String, String> map = new ConcurrentHashMap<>(50);
        Enumeration<Object> keys = propertiesResource.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            map.put(key, propertiesResource.getProperty(key));
        }
        logger.info("加载配置文件:{} succeed" , propertyFileEnum);
        return map;
    }

    /**
     * 从缓存中获取值
     */
    public static String getValueFromResourceFile(String key) {
     return getValue(PropertyFileEnum.RESOURCE_PROPERTIES,key);
    }


    /**
     * 从缓存中获取值(MQ)
     */
    public static String getValueFromMQFile(String key) {
        return getValue(PropertyFileEnum.MQ_PROPERTIES,key);
    }


    /**
     * 从缓存中获取值
     */
    public static String getValue(PropertyFileEnum propName, String key) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key is null");
        }
        switch (propName) {
            case RESOURCE_PROPERTIES:
                return lstValue.get(0).get(key);
            case MQ_PROPERTIES:
                return lstValue.get(3).get(key);
            default:
                throw new IllegalStateException("no support type");
        }
    }

    public enum PropertyFileEnum {
        RESOURCE_PROPERTIES,MQ_PROPERTIES
    }

    public static String getDelGegex(){
        if(delGegex==null) {
            StringBuilder sbDelGegex = new StringBuilder();
            getShieldValue();
            sbDelGegex.append("(\\b(");
            for (String s : shieldListValue) {
                sbDelGegex.append(String.format("%s|", s));

            }
            sbDelGegex.append("aaaa)\\b)");
            delGegex=sbDelGegex.toString();
        }
        return delGegex;
    }

    public static List<String> getShieldValue(){
    	Properties properties=null;
		try {
//			if(shieldListValue.size()<=0){
				properties = PropertiesLoaderUtils.loadAllProperties(SHIELD_FILE);
				Enumeration<?> e = properties.propertyNames();
	    	    while (e.hasMoreElements()) {
	    	        String key = (String) e.nextElement();
	    	        shieldListValue.add(key.trim());
	    	    }
//			}
		} catch (IOException ioe) {
            logger.error("！！！！！！！！！！！！！！读取配置文件失败", ioe);
        }
		return shieldListValue;
    }
    public static void getCategoryValue(){
    	Properties properties=null;
    	try {
//			if(shieldListValue.size()<=0){
    		properties = PropertiesLoaderUtils.loadAllProperties(CATEGORY_FILE);
    		categoryAddListValue = new CopyOnWriteArrayList<>();
    		categoryReplaceListValue = new CopyOnWriteArrayList<>();
    		Enumeration<?> e = properties.propertyNames();
    		while (e.hasMoreElements()) {
    			String key = (String) e.nextElement();
    			String property = properties.getProperty(key);
    			String[] keys = property.split("\\:");
    			if(keys.length == 4) {
    				Map<String,String> map = new HashMap<>();
    				map.put("reg", keys[1]);
    				map.put("categoryName", keys[2]);
    				map.put("href", keys[3]);
    				if("add".equals(keys[0])) {
    					categoryAddListValue.add(map);
    				}else {
    					categoryReplaceListValue.add(map);
    				}
    			}

    		}
//			}
    } catch (IOException ioe) {
    	logger.error("！！！！！！！！！！！！！！读取配置文件失败", ioe);
    }
    }

    public static List<Map<String,String>> getCategoryAddListValue(){
        if(categoryAddListValue==null) {
        	getCategoryValue();
        }
        return categoryAddListValue;
    }

    public static List<Map<String,String>> getCategoryReplaceListValue(){
        if(categoryReplaceListValue==null ) {
        	getCategoryValue();
        }
        return categoryReplaceListValue;
    }

    public static boolean filterTheAltAndTitle(String goodsPid) {
        return ArrayUtils.contains(pidLists,goodsPid);
    }
}