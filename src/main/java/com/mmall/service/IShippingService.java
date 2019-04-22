package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface IShippingService {
    ServerResponse<Map> add(@Param("userId") Integer userId,@Param("shippingId") Shipping shipping);
    ServerResponse<String> del(Integer userId,Integer shippingId);
    ServerResponse<String> update(Integer userId, Shipping shipping);
    ServerResponse<Shipping> select(Integer userId,Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
