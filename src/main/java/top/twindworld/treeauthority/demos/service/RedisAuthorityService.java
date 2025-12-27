package top.twindworld.treeauthority.demos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.twindworld.treeauthority.demos.domain.po.SysFunction;
import top.twindworld.treeauthority.demos.domain.po.SysRole;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RedisAuthorityService {
    private static final String USER_ROLE_KEY_PATTERN = "user:%s:roles";
    private static final String ROLE_FUNC_KEY_PATTERN = "role:%s:funcs";

    private final SysRoleService sysRoleService;
    private final SysFunctionService sysFunctionService;
    private final StringRedisTemplate stringRedisTemplate;

    public Set<String> getUserFunctionKeys(Long userId) {
        Set<String> roleKeys = getUserRoleKeys(userId);
        if (roleKeys.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> functionKeys = new HashSet<>();
        for (String roleKey : roleKeys) {
            Set<String> roleFuncs = stringRedisTemplate.opsForSet().members(roleFuncKey(roleKey));
            if (roleFuncs != null) {
                functionKeys.addAll(roleFuncs);
            }
        }
        return functionKeys;
    }

    public void refreshAll() {
        List<SysRole> roles = sysRoleService.list();
        for (SysRole role : roles) {
            refreshRoleFunctions(role);
        }
    }

    public void refreshRoleFunctions(SysRole role) {
        if (role == null) {
            return;
        }
        List<SysFunction> functions = sysFunctionService.getFunctionsByRoleId(role.getId());
        Set<String> functionKeys = new HashSet<>();
        for (SysFunction function : functions) {
            functionKeys.add(function.getFunctionKey());
        }
        String key = roleFuncKey(role.getRoleKey());
        stringRedisTemplate.delete(key);
        if (!functionKeys.isEmpty()) {
            stringRedisTemplate.opsForSet().add(key, functionKeys.toArray(new String[0]));
        }
    }

    private Set<String> getUserRoleKeys(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        Set<String> cached = stringRedisTemplate.opsForSet().members(userRoleKey(userId));
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        List<SysRole> roles = sysRoleService.getRolesByUserId(userId);
        Set<String> roleKeys = new HashSet<>();
        for (SysRole role : roles) {
            roleKeys.add(role.getRoleKey());
        }
        String key = userRoleKey(userId);
        stringRedisTemplate.delete(key);
        if (!roleKeys.isEmpty()) {
            stringRedisTemplate.opsForSet().add(key, roleKeys.toArray(new String[0]));
        }
        return roleKeys;
    }

    private String userRoleKey(Long userId) {
        return String.format(USER_ROLE_KEY_PATTERN, userId);
    }

    private String roleFuncKey(String roleKey) {
        return String.format(ROLE_FUNC_KEY_PATTERN, roleKey);
    }
}
