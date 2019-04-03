package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    Cart selectByUserIdProductId(@Param("userId")Integer userId,@Param("productId")Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatus(Integer userId);

    //传String ，mybatis会自动将String 转为 int 进行对比?还是String 转成的 varchar可以和int 对比?
    //应该是因为数据库int可以直接和varchar对比.
    int deleteByUserIdProductIds(@Param("userId") Integer userId,@Param("productIdList") List<String> productIdList);

    int checkedOrUnchecked(@Param("userId")Integer userId,@Param("productId")Integer productId,@Param("checked")Integer checked);

    //null无法赋值给基本类型，如果返回为null会报错
    int getCartProductCount(Integer userId);

}