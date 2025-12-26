package top.twindworld.treeauthority.demos.aop.interceptor.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.twindworld.treeauthority.demos.aop.interceptor.NoAuthorityException;
import top.twindworld.treeauthority.demos.domain.Result;

/**
 * 全局异常处理器
 * 作用：拦截 Controller 层抛出的所有异常，转换成 JSON 返回给前端
 */
@RestControllerAdvice 
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 1. 拦截我们自定义的业务异常
     */
    @ExceptionHandler(NoAuthorityException.class)
    public Result<?> handleBizException(NoAuthorityException e) {
        log.warn("你没有对应权限: {}", e.getMsg());
        // 返回 JSON：{ "code": 500, "msg": "用户不存在", "data": null }
        return Result.error(e.getCode(), e.getMsg());
    }

    /**
     * 2. 拦截所有未知的系统异常 (兜底)
     * 防止空指针、SQL错误等堆栈信息直接暴露给前端
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统未知异常", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }

}