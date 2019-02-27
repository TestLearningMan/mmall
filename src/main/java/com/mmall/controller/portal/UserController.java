package com.mmall.controller.portal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user/")
public class UserController {
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value="/login.do",method= RequestMethod.POST)
    @ResponseBody //将返回结果自动序列化成json格式
    public Object login(String username,String password){

        return null;
    }
}
