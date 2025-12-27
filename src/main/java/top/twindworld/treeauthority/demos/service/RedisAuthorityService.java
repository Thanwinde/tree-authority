package top.twindworld.treeauthority.demos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.twindworld.treeauthority.demos.domain.po.SysFunction;
import top.twindworld.treeauthority.demos.domain.po.SysRole;
import top.twindworld.treeauthority.demos.domain.po.SysRoleFunction;
import top.twindworld.treeauthority.demos.mapper.SysRoleFunctionMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisAuthorityService {
    private static final String USER_ROLE_KEY_PREFIX = "user:";
    private static final String USER_ROLE_KEY_SUFFIX = ":roles";
    private static final String ROLE_FUNC_KEY_PREFIX = "role:";
    private static final String ROLE_FUNC_KEY_SUFFIX = ":func";

    private final StringRedisTemplate redisTemplate;
    private final SysRoleService sysRoleService;
    private final SysFunctionService sysFunctionService;
    private final SysRoleFunctionMapper sysRoleFunctionMapper;

    public Set<String> getUserRoleKeys(Long userId) {
        String key = USER_ROLE_KEY_PREFIX + userId + USER_ROLE_KEY_SUFFIX;
        Set<String> roleKeys = redisTemplate.opsForSet().members(key);
        if (roleKeys != null && !roleKeys.isEmpty()) {
            return roleKeys;
        }
        List<SysRole> roles = sysRoleService.getRolesByUserId(userId);
        if (roles.isEmpty()) {
            return new HashSet<>();
        }
        List<String> roleKeyList = new ArrayList<>();
        for (SysRole role : roles) {
            if (role.getRoleKey() != null) {
                roleKeyList.add(role.getRoleKey());
            }
        }
        if (!roleKeyList.isEmpty()) {
            redisTemplate.opsForSet().add(key, roleKeyList.toArray(new String[0]));
        }
        return new HashSet<>(roleKeyList);
    }

    public Set<String> getRoleFunctionKeys(String roleKey) {
        String key = ROLE_FUNC_KEY_PREFIX + roleKey + ROLE_FUNC_KEY_SUFFIX;
        Set<String> funcs = redisTemplate.opsForSet().members(key);
        if (funcs != null && !funcs.isEmpty()) {
            return funcs;
        }
        Set<String> computed = computeRoleFunctionKeys(roleKey);
        refreshRoleFunctionCache(roleKey, computed);
        return computed;
    }

    public boolean addRoleFunction(String roleKey, String functionKey) {
        SysRole role = findRoleByKey(roleKey);
        SysFunction function = findFunctionByKey(functionKey);
        if (role == null || function == null) {
            return false;
        }
        if (sysRoleFunctionMapper.countByRoleAndFunction(role.getId(), function.getId()) > 0) {
            return true;
        }
        sysRoleFunctionMapper.insertRoleFunction(role.getId(), function.getId());
        refreshRoleAndAncestors(role.getId());
        return true;
    }

    public boolean removeRoleFunction(String roleKey, String functionKey) {
        SysRole role = findRoleByKey(roleKey);
        SysFunction function = findFunctionByKey(functionKey);
        if (role == null || function == null) {
            return false;
        }
        if (sysRoleFunctionMapper.countByRoleAndFunction(role.getId(), function.getId()) == 0) {
            return true;
        }
        sysRoleFunctionMapper.deleteRoleFunction(role.getId(), function.getId());
        refreshRoleAndAncestors(role.getId());
        return true;
    }

    private void refreshRoleAndAncestors(Long roleId) {
        Map<Long, SysRole> roleById = loadRolesById();
        Long currentId = roleId;
        while (currentId != null) {
            SysRole current = roleById.get(currentId);
            if (current == null) {
                break;
            }
            Set<String> computed = computeRoleFunctionKeys(current.getRoleKey());
            refreshRoleFunctionCache(current.getRoleKey(), computed);
            currentId = current.getParentId();
        }
    }

    private void refreshRoleFunctionCache(String roleKey, Set<String> functionKeys) {
        String key = ROLE_FUNC_KEY_PREFIX + roleKey + ROLE_FUNC_KEY_SUFFIX;
        redisTemplate.delete(key);
        if (functionKeys != null && !functionKeys.isEmpty()) {
            redisTemplate.opsForSet().add(key, functionKeys.toArray(new String[0]));
        }
    }

    private Set<String> computeRoleFunctionKeys(String roleKey) {
        SysRole role = findRoleByKey(roleKey);
        if (role == null) {
            return new HashSet<>();
        }
        List<SysRole> roles = sysRoleService.list();
        Map<Long, List<SysRole>> childrenByParent = new HashMap<>();
        for (SysRole item : roles) {
            childrenByParent.computeIfAbsent(item.getParentId(), k -> new ArrayList<>()).add(item);
        }
        Set<Long> roleIds = new HashSet<>();
        Deque<Long> queue = new ArrayDeque<>();
        queue.add(role.getId());
        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            if (!roleIds.add(currentId)) {
                continue;
            }
            List<SysRole> children = childrenByParent.get(currentId);
            if (children != null) {
                for (SysRole child : children) {
                    queue.add(child.getId());
                }
            }
        }
        Map<Long, String> functionKeyById = new HashMap<>();
        for (SysFunction function : sysFunctionService.list()) {
            functionKeyById.put(function.getId(), function.getFunctionKey());
        }
        Set<String> result = new HashSet<>();
        List<SysRoleFunction> roleFunctions = sysFunctionService.getAllRoleFunctions();
        for (SysRoleFunction roleFunction : roleFunctions) {
            if (roleIds.contains(roleFunction.getRoleId())) {
                String functionKey = functionKeyById.get(roleFunction.getFunctionId());
                if (functionKey != null) {
                    result.add(functionKey);
                }
            }
        }
        return result;
    }

    private SysRole findRoleByKey(String roleKey) {
        return sysRoleService.lambdaQuery()
                .eq(SysRole::getRoleKey, roleKey)
                .one();
    }

    private SysFunction findFunctionByKey(String functionKey) {
        return sysFunctionService.lambdaQuery()
                .eq(SysFunction::getFunctionKey, functionKey)
                .one();
    }

    private Map<Long, SysRole> loadRolesById() {
        Map<Long, SysRole> roleById = new HashMap<>();
        for (SysRole role : sysRoleService.list()) {
            roleById.put(role.getId(), role);
        }
        return roleById;
    }
}
