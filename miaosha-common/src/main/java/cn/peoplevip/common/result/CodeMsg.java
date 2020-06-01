package cn.peoplevip.common.result;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 11:13
 * 功能
 */
public class CodeMsg {
    private int code;
    private String msg;

    private  CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通用异常
     */
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101,"参数校验异常: %S");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102,"请求非法，路径错误");
    public static CodeMsg VERIFY_ERR = new CodeMsg(500102,"error，验证码错误");
    public static CodeMsg ACCESS_LIMIT_REACHED= new CodeMsg(500104, "访问太频繁！");

    //登录模块异常 500200
    public static CodeMsg TOKEN_ERROR = new CodeMsg(500210,"token不存在或已失效");
    public static CodeMsg PASSWORD_EMOTY = new CodeMsg(500211,"密码为空或长度不符");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500212,"密码不正确");

    public static CodeMsg USER_EMOTY = new CodeMsg(500213,"用户名长度不符合要求");
    public static CodeMsg USER_NOT_EXIST = new CodeMsg(500214,"用户不存在");
    public static CodeMsg USER_NOT_LOGIN = new CodeMsg(500215,"用户未登录");
    public static CodeMsg USER_NOT_ADMIN = new CodeMsg(500216,"用户不是管理员");
    public static CodeMsg VERIFY_CODE_ERROR = new CodeMsg(500217,"登录验证码错误");
    public static CodeMsg VERIFY_CODE_EMOTY = new CodeMsg(500218,"登录验证码已失效");

    //商品模块 500300
    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
    public static CodeMsg REPEATE_MIAOSHA = new CodeMsg(500501, "您已经秒杀过该商品");
    public static CodeMsg MIAO_SHA_NOFIND = new CodeMsg(500502, "商品不存在");
    public static CodeMsg MIAO_SHA_FLASH = new CodeMsg(500506, "秒杀商品不存在,请刷新页面重试");
    public static CodeMsg MIAO_SHA_START = new CodeMsg(500507, "秒杀未开始");
    public static CodeMsg MIAO_SHA_END = new CodeMsg(500508, "秒杀已结束");

    public static CodeMsg ORDER_DATAILNOFOUND = new CodeMsg(500503, "订单不存在");
    public static CodeMsg MIAOSHA_FAIL = new CodeMsg(500504, "秒杀失败");
    public static CodeMsg DATA_NOT_UPDATE = new CodeMsg(500505, "数据未更新");


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

}
