package top.twindworld.treeauthority.demos.aop.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import top.twindworld.treeauthority.demos.aop.annotation.CurrentUserId;
import top.twindworld.treeauthority.demos.aop.annotation.RequirePermission;
import top.twindworld.treeauthority.demos.aop.interceptor.NoAuthorityException;
import top.twindworld.treeauthority.demos.service.RedisAuthorityService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
public class PermissionAspect {
    private final RedisAuthorityService redisAuthorityService;

    public PermissionAspect(RedisAuthorityService redisAuthorityService) {
        this.redisAuthorityService = redisAuthorityService;
    }

    /**
     * 拦截所有加上了 @RequirePermission 注解的方法
     */
    @Before("@annotation(top.twindworld.treeauthority.demos.aop.annotation.RequirePermission)")
    public void checkPermission(JoinPoint point) {
        // 1. 获取注解详情
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        if (annotation == null) {
            return;
        }

        // 2. 获取当前登录用户拥有的权限集合 (从 请求 中拿)

        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        // 获取所有参数值
        Object[] args = point.getArgs();
        Long currentUserId = null;
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation a : paramAnnotations[i]) {
                if (a instanceof CurrentUserId) {
                    // 找到了！返回对应的参数值
                    currentUserId = (Long) args[i];
                    break;
                }
            }
        }
        if (currentUserId == null) {
            throw new NoAuthorityException(401,"无法获取当前用户 ID，权限校验失败");
        }
        Set<String> roleKeys = redisAuthorityService.getUserRoleKeys(currentUserId);
        if (roleKeys == null || roleKeys.isEmpty()) {
            throw new NoAuthorityException(403, "用户暂无角色权限");
        }

        // 3. 获取接口要求的权限
        String[] requiredPerms = annotation.value();
        RequirePermission.Logical logical = annotation.logical();

        // 4. 开始校验
        HashSet<String> functionSet = new HashSet<>();
        for (String roleKey : roleKeys) {
            functionSet.addAll(redisAuthorityService.getRoleFunctionKeys(roleKey));
        }
        check(functionSet, requiredPerms, logical);
    }

    /**
     * 具体的校验逻辑
     */
    private void check(Set<String> userHas, String[] mustHave, RequirePermission.Logical logical) {
        if (mustHave.length == 0) {
            return;
        }

        // 逻辑 AND: 必须包含所有要求的权限
        if (logical == RequirePermission.Logical.AND) {
            for (String perm : mustHave) {
                if (!userHas.contains(perm)) {
                    throw new NoAuthorityException("无权访问: 缺少权限 [" + perm + "]");
                }
            }
        } 
        // 逻辑 OR: 只要包含其中一个即可
        else {
            boolean hasAny = Arrays.stream(mustHave).anyMatch(userHas::contains);
            if (!hasAny) {
                throw new NoAuthorityException("无权访问: 需要权限之一 " + Arrays.toString(mustHave));
            }
        }
    }
}
