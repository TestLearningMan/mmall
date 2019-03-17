package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements IProductService {
    public ServerResponse saveOrUpdateProduct(Product product){
        if (product==null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray=product.getSubImages().split(",");
                if (subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
                if (product.getId()!=null){
                    //id不为null，说明是更新操作
                }

            }

        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }
}
