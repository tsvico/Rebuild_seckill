package cn.peoplevip.other.exception;

import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/26 14:52
 * 全局异常拦截器
 */
@ControllerAdvice
@ResponseBody
public class GlobleExceptionHandler {

    /**
     * 所有异常都拦截
     *
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e) {
        //打印异常
        e.printStackTrace();
        //绑定异常
        if (e instanceof GlobleException) {
            GlobleException ex = (GlobleException) e;
            return Result.error(ex.getCodeMsg());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            StringBuilder msg = new StringBuilder(10);
            for (ObjectError error : errors) {
                msg.append(error.getDefaultMessage()).append("\n");
            }
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg.toString()));
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
