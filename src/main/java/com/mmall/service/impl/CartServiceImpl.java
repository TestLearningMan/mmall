package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.service.IFileService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import com.mmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Spliterators;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo=getCartLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> add(Integer userId,Integer count,Integer productId){
        if (productId == null || userId == null || productId == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectByUserIdProductId(userId,productId);
        if (cart == null ){
            //说明此产品不在购物车中，需要新增一个产品记录
            cart=new Cart();
            cart.setQuantity(count);
            cart.setProductId(productId);
            cart.setChecked(Const.Cart.CHECKED);
            cart.setUserId(userId);
            cartMapper.insert(cart);
        }else {
            //这个产品已经在购物车中
            //如果产品已存在，数量相加
            count=count+cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }


    public ServerResponse<CartVo> update(Integer userId,Integer count,Integer productId) {
        if (userId == null || count == null || productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        //当cart为null时，updateByPrimaryKeySelective方法是否会报错？
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        List<String> productIdList= Splitter.on(",").splitToList(productIds);
        if (productIdList.size()==0){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int count= cartMapper.deleteByUserIdProductIds(userId,productIdList);
        if (count >0){
            return this.list(userId);
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }

    //使用常量Const.Cart.Unchecked/checked，可以随时修改选择标志，避免前端传错参数
    //productId传Null就可以实现全选功能，和单选功能
    public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked){
        int resultCount=cartMapper.checkedOrUnchecked(userId,productId,checked);
        if (resultCount==0){
            return  ServerResponse.createByErrorMessage("当前购物车无此商品");
        }
        return this.list(userId);
    }

    /**
     * 获取购物车商品数量
     * @param userId
     * @return
     */
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        //controller判断了user，但是userId也有可能是null
        if (userId == null){
            return  ServerResponse.createBySuccess(0);
        }
        int count = cartMapper.getCartProductCount(userId);
        return ServerResponse.createBySuccess(count);
    }







    private CartVo getCartLimit(Integer userId){
        //在一开始就需要把计算参数、返回结果创建出来，这样无论中间逻辑判断怎样。都会有一个返回值
        CartVo cartVo=new CartVo();
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList= Lists.newArrayList();
        //在商业计算中，必须要用BigDecimal的String构造器，否则会出现精度误差
        //详见Bigdecimal的构造方法
        BigDecimal cartTotlePrice=new BigDecimal("0");
        if (cartList !=null){
            for (Cart cartItem : cartList){
            CartProductVo cartProductVo=new CartProductVo();
            cartProductVo.setCartId(cartItem.getId());
            cartProductVo.setUserId(cartItem.getUserId());
            cartProductVo.setProductId(cartItem.getProductId());

            Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (product != null){
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                cartProductVo.setProductStock(product.getStock());
                //判断库存;
                int buyLimitCount=0;
                if (product.getStock()>=cartItem.getQuantity()) {
                    buyLimitCount=cartItem.getQuantity();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                }else {
                    buyLimitCount=product.getStock();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    //购物车中更新有效库存
                    //只需要更新库存，所以用updateByPrimaryKeySelective方法，并放入只有库存的对象
                    Cart cartForQuantity=new Cart();
                    cartForQuantity.setId(cartItem.getId());
                    cartForQuantity.setQuantity(product.getStock());
                    cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                }
                cartProductVo.setQuantity(buyLimitCount);
                cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity().doubleValue()));
                cartProductVo.setProductChecked(cartItem.getChecked());
            }
            //如果productId查不到product，则ProductChecked为null,不通过校验？
            if (cartProductVo.getProductChecked() == Const.Cart.CHECKED){
                cartTotlePrice=BigDecimalUtil.add(cartTotlePrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
            }
            cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotlePrice);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        return cartVo;
    }


    private boolean getAllCheckedStatus(Integer userId){
        if (userId == null){
            return  false;
        }
        return cartMapper.selectCartProductCheckedStatus(userId)==0?true:false;
    }





}
