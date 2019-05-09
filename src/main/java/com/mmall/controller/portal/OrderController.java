package com.mmall.controller.portal;



import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mysql.jdbc.log.LogFactory;
import jdk.incubator.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    IOrderService iOrderService;

    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);


    @RequestMapping(value="create.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse create(HttpSession session,Integer shippingId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return null;
    }

    @RequestMapping(value = "pay.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,String>> pay(HttpSession session , Long orderNo, HttpServletRequest request){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //此处获取到的path最后一个字符不是 /
        String path=request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    @RequestMapping(value = "alipay_callback.do",method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request){
        Map<String,String[]> requestParams=request.getParameterMap();
        Map params= Maps.newHashMap();
        for (Iterator iterator = requestParams.keySet().iterator();iterator.hasNext();) {

            String name = (String) iterator.next();
            String[] value = requestParams.get(name);
            String valueStr="";
            for (int i=0;i<value.length;i++){
                valueStr= i== value.length - 1 ? valueStr + value[i] : valueStr+value[i] + ",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        params.remove("sign_type");
        try {
                  boolean alipayRSACheckedV2 = false;
                  alipayRSACheckedV2  = AlipaySignature.rsaCheckV2(params, Configs.getPublicKey(),"utf-8", Configs.getSignType());
                  if (!alipayRSACheckedV2){
                        return ServerResponse.createByErrorMessage("非法请求，再次请求报警");
                  }
        } catch (AlipayApiException e) {
            logger.info("支付宝验证回调异常",e);
        }

        //Todo 各种数据验证
        ServerResponse response=iOrderService.aliCallBack(params);
        if (response.isSuccess()){
            return Const.AlipayCallBack.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallBack.RESPONSE_FAILED;
    }

    @RequestMapping(value = "query_order_pay_status",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(Long orderNo,HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        Boolean result = iOrderService.queryOrderPayStatus(user.getId(),orderNo).isSuccess();
        return ServerResponse.createBySuccess(result);
    }


}
