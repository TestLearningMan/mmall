package com.mmall.dao;

import com.mmall.pojo.Cart;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);
    /*
     insert是将整个对象插入数据库表，insertSelective是将不为空的属性插入到数据库表中
     */

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    int insertAllProduct();
}