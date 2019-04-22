package com.mmall.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    private BigDecimalUtil(){
    }

    /**
     * 加法
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal add(double a,double b){
        BigDecimal b1=new BigDecimal(Double.toString(a));
        BigDecimal b2=new BigDecimal(Double.toString(b));
        return b1.add(b2);
    }

    /**
     * 减法
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal sub(double a,double b){
        BigDecimal b1=new BigDecimal(Double.toString(a));
        BigDecimal b2=new BigDecimal(Double.toString(b));
        return b1.subtract(b2);
    }

    /**
     * 乘法
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal mul(double a,double b){
        BigDecimal b1=new BigDecimal(Double.toString(a));
        BigDecimal b2=new BigDecimal(Double.toString(b));
        return b1.multiply(b2);
    }

    /**
     * 除法
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal dix(double a,double b){
        BigDecimal b1=new BigDecimal(Double.toString(a));
        BigDecimal b2=new BigDecimal(Double.toString(b));
        return b1.divide(b2,BigDecimal.ROUND_HALF_UP);//四舍五入，保留两位小数
    }



}
