package com.mmall.util;

import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by geely
 */
public class PropertiesUtil {
    private static Logger logger=(Logger) LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties props;

    //执行顺序  静态代码块>普通代码块>构造器代码块，静态代码块只会执行一次
    //jdbc class.forName("com.mysql.jdbc.Driver")也是类似原理。在执行此方法时，会加载
    //Driver类，驱动类中有静态代码块会注册驱动.
    static{
        String fileName="mmall.properties";
        props=new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"utf-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            return defaultValue;
        }
        return value.trim();
    }

}
