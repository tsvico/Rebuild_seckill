package cn.peoplevip.other.Service;

import cn.peoplevip.common.Utils.AESSecretUtil;
import cn.peoplevip.common.Utils.MD5Util;
import cn.peoplevip.common.Utils.UUIDUtil;
import cn.peoplevip.common.api.MiaoshaUserService;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.redisKey.MiaoshaUserKey;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.vo.LoginVo;
import cn.peoplevip.other.dao.MiaoshaUserDao;
import cn.peoplevip.other.exception.GlobleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/13 17:17
 * 功能
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class MiaoshauserServiceImpl implements MiaoshaUserService {
    public static final String COOKI_NAME_TOKEN = "Authorization";

    @Value("${aespass}")
    String aesPass;

    @Autowired(required = false)
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;


    @Override
    public MiaoshaUser getById(long id) {
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (user != null) {
            return user;
        }

        user = miaoshaUserDao.getById(id);
        if (user != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, user);
        }
        return user;
    }

    /**
     * 更新密码
     *
     * @param id 用户ID
     * @param passwordNew 新密码
     * @return 是否成功
     */
    @Override
    public boolean updatePassword(String token, long id, String passwordNew) {
        MiaoshaUser user = getById(id);
        if (user == null) {
            throw new GlobleException(CodeMsg.USER_NOT_EXIST);
        }
        //需要更新哪个就设置哪个
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.FormPassToDBPass(passwordNew, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    /**
     * 登录验证
     *
     * @param response 请求头
     * @param loginVo  记录登录的账户和密码
     ***/
    @Override
    public Map<String, Object> login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobleException(CodeMsg.SERVER_ERROR);
        }
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        //验证用户是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(username));
        if (miaoshaUser == null) {
            throw new GlobleException(CodeMsg.USER_NOT_EXIST);
        }
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String dbSlat = miaoshaUser.getSalt();
        String calcPass = MD5Util.FormPassToDBPass(password, dbSlat);
        if (!calcPass.equals(dbPass)) {
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
        HashMap<String, Object> res = new HashMap<>(3);
        //增加登录次数
        updateLoginCount(Long.parseLong(username));
        //如果登陆成功，生成一个cookie
        String token = UUIDUtil.uuid() + "";
        addCookie(miaoshaUser, token, response);
        res.put("token", token);
        res.put("user", miaoshaUser);
        return res;
    }

    /**
     * 压力测试批量生成用户Token
     *
     * @return token
     */
    @Override
    public String testLogin(String username, String password) {
        if (username == null || password == null) {
            return "用户名或密码空";
        }
        //验证用户是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(username));
        if (miaoshaUser == null) {
            throw new GlobleException(CodeMsg.USER_NOT_EXIST);
        }
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        //String dbSlat = miaoshaUser.getSalt();
        //String calcPass = MD5Util.FormPassToDBPass(password, dbSlat);
        if (!password.equals(dbPass)) {
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
        //增加登录次数
        updateLoginCount(Long.parseLong(username));
        //如果登陆成功，生成一个cookie
        String token = UUIDUtil.uuid() + "";
        //字符串存储
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
        return AESSecretUtil.encryptToBase64Str(token,aesPass);
    }

    /**
     * 增加登录次数
     * @param parseLong 用户ID
     */
    @Override
    public void updateLoginCount(long parseLong) {
        miaoshaUserDao.updateLoginCount(parseLong);
    }

    @Override
    public MiaoshaUser getByToken(String token, HttpServletResponse response) {
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

    private String getCookieValue(HttpServletRequest request, String cookiNameToken) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookiNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 获取请求中的token
     *
     * @param request 请求体
     * @return token
     */
    @Override
    public String getLoginToken(HttpServletRequest request) {
        //从请求参数中取出数据,例如表单
        String ParamToken = request.getParameter(COOKI_NAME_TOKEN);
        //从cookie中取出数据,不使用cookie，防止CSRF攻击
        //String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
        //从header中取出数据
        String headerToken = request.getHeader(COOKI_NAME_TOKEN);

        if (StringUtils.isEmpty(ParamToken) && StringUtils.isEmpty(headerToken)) {
            return null;
        }
        //优先级 header  > 参数
        return StringUtils.isEmpty(headerToken) ? ParamToken : headerToken;
    }
    @Override
    public String getLoginToken(String ParamToken, String headerToken){
        if (StringUtils.isEmpty(ParamToken) && StringUtils.isEmpty(headerToken)) {
            return null;
        }
        //优先级 header > cookies > 参数
        String token = StringUtils.isEmpty(headerToken) ? ParamToken : headerToken;
        return token;
    }
}
