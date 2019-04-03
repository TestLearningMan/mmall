package com.mmall.common;
import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;//管理员
    }

    public interface Cart{
        int CHECKED=1; //购物车选中状态
        int UN_CHECKED=0;//即购物车未选中状态

        String LIMIT_NUM_FAIL="LIMIT_NUM_FILE";
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
    }


    public interface ProductListOrderBy{
        //Set的contain方法时间复杂度是O1，List的contain方法的时间复杂度是ON
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("PRICE_DESC","PRICE_ASC");
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");

        int code;
        String value;

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        private ProductStatusEnum(int code, String value){
        this.code=code;
        this.value = value;

        }

    }

    }

