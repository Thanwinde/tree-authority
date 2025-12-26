package top.twindworld.treeauthority.demos.AOP.interceptor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMsg());
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
        // 返回 JSON：{ "code": 999, "msg": "系统繁忙，请稍后重试", "data": null }
        return Result.error(999, "系统繁忙，请稍后重试");
    }
    
    /**
     * 3. 拦截特定异常，例如权限不足 (结合你之前的 RBAC)
     */
    /*
    @ExceptionHandler(AccessDeniedException.class) // 如果用 Spring Security
    public Result<?> handleAccessDeniedException(Exception e) {
        return Result.error(403, "您没有权限执行此操作");
    }
    */
}