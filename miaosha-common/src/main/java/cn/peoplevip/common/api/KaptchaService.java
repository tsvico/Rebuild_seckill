package cn.peoplevip.common.api;

import cn.peoplevip.common.domain.MiaoshaUser;

import java.awt.image.BufferedImage;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/13 22:21
 * 功能
 */
public interface KaptchaService {
    boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode);
    BufferedImage createVerifyCode(MiaoshaUser user, long goodsId);
}
