package com.mmall.common;

public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(2,"NEED LOGIN"),
    ILLEGAL(3,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String msg;
     ResponseCode(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
