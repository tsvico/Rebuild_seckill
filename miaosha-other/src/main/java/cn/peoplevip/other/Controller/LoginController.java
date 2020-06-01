package cn.peoplevip.other.Controller;

import cn.peoplevip.common.Utils.AESSecretUtil;
import cn.peoplevip.common.api.MiaoshaUserService;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.redisKey.MiaoshaUserKey;
import cn.peoplevip.common.redisKey.UserKey;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import cn.peoplevip.common.vo.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 11:07
 */
@Api(value = "登录相关")
@Controller
@RequestMapping("/user")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Value("${aespass}")
    String aesPass;

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    //@Valid LoginVo loginVo
    //@RequestBody @Validated LoginVo loginVo
    @ApiOperation(value = "用户登录返回token", notes = "需要username,password,verifyCodetoken")
    @PostMapping("/login")
    @ResponseBody
    public Result<Object> login(@Valid LoginVo loginVo,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                @CookieValue(value = "verifyCodetoken", required = false) String verifyCodetoken) {
        String verifyCode = loginVo.getVerifyCodeActual();
        if (verifyCodetoken == null) {
            //cookie中没有就尝试从请求头中获取
            verifyCodetoken = request.getHeader("verifyCodetoken");
        }
        if (verifyCodetoken == null) {
            return Result.error(CodeMsg.VERIFY_CODE_EMOTY);
        }
        String redisVerifyCode = redisService.get(UserKey.getkaptcha, verifyCodetoken, String.class);
        if (!verifyCode.equals(redisVerifyCode)) {
            //验证验证码
            return Result.error(CodeMsg.VERIFY_CODE_ERROR);
        }
        //登录,登陆中的错误在server中处理完毕，返回值无用
        Map<String,Object> res = miaoshaUserService.login(response, loginVo);
        String token = (String) res.get("token");
        res.put("token", AESSecretUtil.encryptToBase64Str(token,aesPass));
        res.remove("user");
        //HashMap<String, String> data = new HashMap<>();
        //data.put("token", token);
        return Result.success(res);
    }

    @ApiOperation(value = "登出功能")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public Result<CodeMsg> logout(HttpServletRequest request) {
        // System.out.println(miaoshaUserService);

        String token = miaoshaUserService.getLoginToken(request);
        if (token != null) {
            redisService.delete(MiaoshaUserKey.token, token);
            return Result.success(null);
        } else {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }

    }


    /**
     * 压力测试
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping("/userinfo")
    @ResponseBody
    public Result<MiaoshaUser> userInfo(MiaoshaUser user) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        user.setPassword(null);
        user.setLoginCount(null);
        user.setSalt(null);
        return Result.success(user);
    }

}
