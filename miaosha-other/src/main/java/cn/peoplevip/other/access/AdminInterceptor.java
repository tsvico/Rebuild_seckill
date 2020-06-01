package cn.peoplevip.other.access;

import cn.peoplevip.common.Utils.UserContext;
import cn.peoplevip.common.api.AdminService;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/11 8:42
 * 管理员登录拦截器
 */
@Service
public class AdminInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    AdminService adminService;
    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MiaoshaUser user = UserContext.getUser();
        if (user == null) {
            render(response, CodeMsg.USER_NOT_LOGIN);
            return false;
        }
        if (user.getRole() != 1) {
            render(response, CodeMsg.USER_NOT_ADMIN);
            return false;
        }
        //如果设置为false时，被请求时，拦截器执行到此处将不会继续操作
        return true;
        //如果设置为true时，请求将会继续执行后面的操作
    }

    private void render(HttpServletResponse response, CodeMsg userNotLogin) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(userNotLogin));
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }
}
