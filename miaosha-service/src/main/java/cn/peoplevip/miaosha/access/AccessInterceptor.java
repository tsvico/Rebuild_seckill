package cn.peoplevip.miaosha.access;

import cn.peoplevip.common.Utils.AESSecretUtil;
import cn.peoplevip.common.Utils.UserContext;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.redisKey.AccessKey;
import cn.peoplevip.common.redisKey.MiaoshaUserKey;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/6 15:18
 * 拦截器，用于拦截请求次数
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Value("${aespass}")
    String aesPass;

    public static final String COOKI_NAME_TOKEN = "Authorization";

    @Autowired
    RedisService redisService;

    /**
     * 在业务处理器处理请求之前被调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            //获取登录用户信息
            MiaoshaUser user = getUser(request, response);
            //将user存储在ThreadLocal里
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            //拿到方法上的注解
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            //获取 除去host（域名或者ip）部分的路径
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    render(response, CodeMsg.USER_NOT_LOGIN);
                    return false;
                }
                //redis key的拼接
                key += "_" + user.getId();
            }

            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null) {
                redisService.set(ak, key, 1);
            } else if (count < maxCount) {
                //增加访问次数
                redisService.incr(ak, key);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg userNotLogin) throws Exception {
        //设置响应格式为json
        response.setContentType("application/json;charset=UTF-8");
        //获取字节流
        OutputStream out = response.getOutputStream();
        //转换变量为json格式
        String str = JSON.toJSONString(Result.error(userNotLogin));
        //以byte[]写入
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

    /**
     * 从token中取出user
     *
     * @param request  请求头，用于取出token
     * @param response 响应头，用于获取user，并延长token生效时间
     * @return User
     */
    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String ParamToken = request.getParameter(COOKI_NAME_TOKEN);
        //getCookieValue(COOKI_NAME_TOKEN);
        //从header中取出数据
        String headerToken = request.getHeader(COOKI_NAME_TOKEN);

        if (StringUtils.isEmpty(ParamToken) && StringUtils.isEmpty(headerToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(headerToken) ? ParamToken : headerToken;
        return getByToken(token,response);
    }

    private MiaoshaUser getByToken(String token, HttpServletResponse response) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        //从redis中获取数据
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (user != null) {
            addCookie(user, token, response);
        }
        return user;
    }

    /**
     * 生成cookie
     *
     * @param miaoshaUser User
     * @param response 响应体
     */
    private void addCookie(MiaoshaUser miaoshaUser, String token, HttpServletResponse response) {
        //if (redisService.)
        //字符串存储
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
        String authorization = AESSecretUtil.encryptToBase64Str(token,aesPass);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, authorization);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
