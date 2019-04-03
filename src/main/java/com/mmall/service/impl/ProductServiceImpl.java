package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper   productMapper;

    @Autowired
    CategoryMapper  categoryMapper;

    @Autowired
    ICategoryService iCategoryService;


    /**
     * 保存|更新商品
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product){
        if (product!=null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray=product.getSubImages().split(",");
                if (subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
                if (product.getId()!=null){
                    //id不为null，说明是更新操作
                    int rowCount=productMapper.updateByPrimaryKey(product);
                    if (rowCount>0){
                        ServerResponse.createBySuccess("更新产品成功");
                    }
                    return  ServerResponse.createByErrorMessage("更新产品失败");
                } else {
                    int rowCount=productMapper.insert(product);
                    if (rowCount>0){
                        ServerResponse.createBySuccess("新增产品成功");
                    }
                    return ServerResponse.createByErrorMessage("新增产品失败");
                }
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    /**
     *
     * @param productId
     * @param status
     * @return
     */
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if (productId == null || status == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKeySelective(product);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("修改产品销售状态成功");
        }else {
            return ServerResponse.createByErrorMessage("修改产品销售状态失败");
        }

    }

    /**
     *
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product ==null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
//          VO对象--value Object
        //pojo --> bo(business objeect)--> vo(view object)
        return ServerResponse.createBySuccess(assembleProductDetail(product)) ;
    }

    private ProductDetailVo assembleProductDetail(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category==null){
            //如果产品无类别，则默认根节点为其父节点
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        return productDetailVo;
    }

    /**
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo>  getProductList(@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "5") int pageSize){
        //startPage--start
        //填充自己的查询逻辑
        //pageHelper收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectList();
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //向页面对象(包含页数、前一页、后一页等)中填充页面分录
        //能否直接把productListVoList放入pageInfo中创建对象?
        //PageInfo productPageInfo=new PageInfo(productListVoList);
        PageInfo productPageInfo=new PageInfo(productList);
        productPageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(productPageInfo);
    }

    /**
     * 生成产品列表分录
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setId(product.getId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setName(product.getName());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            //用String+时，会占用大量内存，然后导致频繁GC，从而比较慢
            //慢是慢在频繁GC上，而不是慢在+上.
            //StringBuilder不是线程安全的。StringBuffer是线程安全的
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product>productList=productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product ==null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        if (Const.ProductStatusEnum.ON_SALE.getCode()!=product.getStatus()){
            return ServerResponse.createByErrorMessage("产品已下架");
        }
//          VO对象--value Object
        //pojo --> bo(business objeect)--> vo(view object)
        return ServerResponse.createBySuccess(assembleProductDetail(product)) ;
    }



    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy){
        if (org.apache.commons.lang.StringUtils.isBlank(keyword) && categoryId == null){
            return  ServerResponse.createByErrorMessage("参数错误");
        }
        List categoryIdList=Lists.newArrayList();
        if (categoryId != null ) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && keyword == null) {
                //当分类不存在时，直接返回一个空的PageInfo。因为页面参数如 总页数等还是需要的。返回的数据格式不能变
                List list = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(list);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            String[] orderByArray=orderBy.split("_");
            PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
        }
        List<Product> productList=productMapper.selectByNameAndCategoryIds(StringUtils.isNotBlank(keyword)? keyword:null ,categoryIdList.size()>0?categoryIdList:null);
        List<ProductListVo> productListVos=Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        //pageInfo参数要为sql的执行结果
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }



}

