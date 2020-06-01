package cn.peoplevip.other.Controller;

import cn.peoplevip.common.Utils.UUIDUtil;
import cn.peoplevip.common.api.KaptchaService;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.redisKey.UserKey;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 11:07
 */
@Api(value = "验证码相关")
@Controller
public class KaptchaController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    RedisService redisService;

    @Autowired
    KaptchaService kaptchaService;
    /**
     * 验证码生成
     */
    @ApiOperation(value = "登录页面验证码")
    @GetMapping("/kaptcha")
    public void captcha(HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        // 生成文字验证码
        String verifyCode = defaultKaptcha.createText();
        // 生成图片验证码
        BufferedImage image = defaultKaptcha.createImage(verifyCode);
        // 保存到Redis
        String verifyCodetoken = UUIDUtil.uuid() + "";
        redisService.set(UserKey.getkaptcha,verifyCodetoken, verifyCode);

        Cookie cookie = new Cookie("verifyCodetoken", verifyCodetoken);
        //单位秒
        cookie.setMaxAge(60*2);
        cookie.setPath("/");
        response.addCookie(cookie);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        out.flush();
        out.close();
    }

    @ApiOperation(value = "秒杀页面验证码")
    @GetMapping("/seckill/verifyCode")
    @ResponseBody
    public Result<Object> verifyCode(MiaoshaUser user,
                                     @RequestParam("goodsId") long goodsId,
                                     HttpServletResponse response) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }

        BufferedImage image = kaptchaService.createVerifyCode(user,goodsId);
        try(OutputStream out = response.getOutputStream()){
            ImageIO.write(image, "jpg", out);
//        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
//            ImageIO.write(image, "jpg", outputStream);
//            // 对字节数组Base64编码
//            BASE64Encoder encoder = new BASE64Encoder();
//            encoder.encode(outputStream.toByteArray());
//            HashMap<String, String> map = new HashMap<>();
//            map.put("img", encoder.encode(outputStream.toByteArray()));
            return null;//Result.success(map);
        }catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
