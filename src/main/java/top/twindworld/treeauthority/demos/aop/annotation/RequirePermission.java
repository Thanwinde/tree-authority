package top.twindworld.treeauthority.demos.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * 只检查功能标识符 (Function Key)，不检查角色
 */
@Target(ElementType.METHOD) // 作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface RequirePermission {

    /**
     * 需要的权限标识，例如: {"user:add", "user:edit"}
     */
    String[] value();

    /**
     * 校验逻辑：AND (所有权限都要有) / OR (只要有一个权限即可)
     * 默认为 AND
     */
    Logical logical() default Logical.AND;

    enum Logical {
        AND, OR
    }
}