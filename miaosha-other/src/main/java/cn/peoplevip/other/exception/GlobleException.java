package cn.peoplevip.other.exception;

import cn.peoplevip.common.result.CodeMsg;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/26 16:50
 * 功能
 */
public class GlobleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private CodeMsg codeMsg;
    public GlobleException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
