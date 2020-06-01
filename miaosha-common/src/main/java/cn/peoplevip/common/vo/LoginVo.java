package cn.peoplevip.common.vo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/26 0:20
 * 接受参数的方法
 */
public class LoginVo {
    @NotEmpty
    @Digits(integer = 11, fraction = 0)
    private String username;

    @NotEmpty
    @Length(min = 32,max = 32)
    private String password;

    //验证码
    @NotEmpty
    private String verifyCodeActual;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCodeActual() {
        return verifyCodeActual;
    }

    public void setVerifyCodeActual(String verifyCodeActual) {
        this.verifyCodeActual = verifyCodeActual;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", verifyCodeActual='" + verifyCodeActual + '\'' +
                '}';
    }
}
