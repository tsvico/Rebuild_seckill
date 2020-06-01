package cn.peoplevip.common.Utils;

import org.springframework.util.DigestUtils;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/25 22:21
 * 使用SpringBoot自带Md5
 */
public class MD5Util {

    //盐，用于混交md5
    private static String salt = "asdwqAsd1q_sz0";

    /**
     * 生成md5
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    public static String inputPassToFormPass(String inputPass) {
        String str = "" + salt.charAt(0) +
            salt.charAt(4) + inputPass +
            salt.charAt(0) + salt.charAt(1);
        return getMD5(str);
    }

    public static String FormPassToDBPass(String formPass, String salt) {
        String str = formPass + "v" + salt;
        return getMD5(str);
    }

    public static String inputPassToDbPass(String inputPass,String salt){
        return FormPassToDBPass(inputPassToFormPass(inputPass),salt);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123"));
        System.out.println(inputPassToDbPass("123","aabbcc"));
    }
}
