import ch.qos.logback.core.net.SyslogOutputStream;
import com.mmall.dao.CartMapper;
import com.mmall.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class productTest {
    @Autowired
    CartMapper cartMapper;

    @Test
    public void StringTest(){
        String str="";
        str=new StringBuilder().append("%").append(str).append("%").toString();
        System.out.println(str);

    }

    @Test
    public void  BigdecimalTest(){
        System.out.println(0.05+0.01);
        BigDecimal b1=new BigDecimal("0.05");
        BigDecimal b2=new BigDecimal("0.01");
        System.out.println(b1.add(b2));
    }

    @Test
    public void CartNullTest(){
        Cart cart=null;
        cartMapper.updateByPrimaryKeySelective(cart);
    }
}
