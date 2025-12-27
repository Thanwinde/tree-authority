package top.twindworld.treeauthority.demos.service;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.twindworld.treeauthority.demos.domain.dto.SysFunctionDTO;
import top.twindworld.treeauthority.demos.domain.dto.SysRoleDTO;
import top.twindworld.treeauthority.demos.domain.dto.SysUserDTO;
import top.twindworld.treeauthority.demos.domain.po.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisAuthorityService {
    private static final String USER_FUNC_KEY_PATTERN = "user:%s:funcKey";
    private static final String ROLE_USERS_KEY_PATTERN = "role:%s:users";

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysFunctionService sysFunctionService;
    private final StringRedisTemplate stringRedisTemplate;

    public Set<String> getUserFunctionKeys(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        Set<String> members = stringRedisTemplate.opsForSet().members(userFuncKey(userId));
        if (members == null) {
            return Collections.emptySet();
        }
        return members;
    }

    public void refreshAll() {
        HashMap<Long, SysUserDTO> userMap = new HashMap<>();
        HashMap<Long, SysRoleDTO> roleMap = new HashMap<>();
        HashMap<Long, SysFunctionDTO> funcMap = new HashMap<>();
        Map<String, Set<String>> roleUsersMap = new HashMap<>();

        List<SysUser> users = sysUserService.list();
        for (SysUser user : users) {
            SysUserDTO dto = BeanUtil.copyProperties(user, SysUserDTO.class);
            userMap.put(user.getId(), dto);
        }

        List<SysRole> roles = sysRoleService.list();
        for (SysRole role : roles) {
            SysRoleDTO dto = BeanUtil.copyProperties(role, SysRoleDTO.class);
            roleMap.put(role.getId(), dto);
        }

        List<SysFunction> functions = sysFunctionService.list();
        for (SysFunction function : functions) {
            SysFunctionDTO dto = BeanUtil.copyProperties(function, SysFunctionDTO.class);
            funcMap.put(function.getId(), dto);
        }

        List<SysUserRole> sysUserRoles = sysRoleService.getAllUserRoles();
        for (SysUserRole userRole : sysUserRoles) {
            SysUserDTO userDTO = userMap.get(userRole.getUserId());
            SysRoleDTO roleDTO = roleMap.get(userRole.getRoleId());
            if (userDTO != null && roleDTO != null) {
                userDTO.getRoles().add(roleDTO);
                userDTO.getRoleSet().add(roleDTO.getRoleKey());
                roleUsersMap
                    .computeIfAbsent(roleDTO.getRoleKey(), key -> new HashSet<>())
                    .add(String.valueOf(userDTO.getId()));
            }
        }
        for (SysRoleDTO roleDTO : roleMap.values()) {
            roleUsersMap.putIfAbsent(roleDTO.getRoleKey(), new HashSet<>());
        }

        List<SysRoleFunction> sysRoleFunctions = sysFunctionService.getAllRoleFunctions();
        for (SysRoleFunction roleFunction : sysRoleFunctions) {
            SysRoleDTO roleDTO = roleMap.get(roleFunction.getRoleId());
            SysFunctionDTO funcDTO = funcMap.get(roleFunction.getFunctionId());
            if (roleDTO != null && funcDTO != null) {
                roleDTO.getFunctions().put(funcDTO.getFunctionKey(), funcDTO);
            }
        }

        for (SysRoleDTO role : roleMap.values()) {
            if (role.getParentId() != null) {
                collectParentFunctions(roleMap, role);
            }
        }
        for (SysUserDTO user : userMap.values()) {
            for (SysRoleDTO role : user.getRoles()) {
                for (SysFunctionDTO func : role.getFunctions().values()) {
                    user.getFunctions().add(func);
                    user.getFunctionSet().add(func.getFunctionKey());
                }
            }
        }

        for (SysUserDTO user : userMap.values()) {
            String key = userFuncKey(user.getId());
            stringRedisTemplate.delete(key);
            if (!user.getFunctionSet().isEmpty()) {
                stringRedisTemplate.opsForSet()
                    .add(key, user.getFunctionSet().toArray(new String[0]));
            }
        }

        for (Map.Entry<String, Set<String>> entry : roleUsersMap.entrySet()) {
            String key = roleUsersKey(entry.getKey());
            stringRedisTemplate.delete(key);
            if (!entry.getValue().isEmpty()) {
                stringRedisTemplate.opsForSet()
                    .add(key, entry.getValue().toArray(new String[0]));
            }
        }
    }

    public void refreshUserPermissions(Long userId) {
        if (userId == null) {
            return;
        }
        Map<Long, SysRoleDTO> roleMap = buildRoleFunctionMap();
        List<SysRole> roles = sysRoleService.getRolesByUserId(userId);
        Set<String> functionKeys = new HashSet<>();
        for (SysRole role : roles) {
            SysRoleDTO roleDTO = roleMap.get(role.getId());
            if (roleDTO != null) {
                functionKeys.addAll(roleDTO.getFunctions().keySet());
            }
        }
        String key = userFuncKey(userId);
        stringRedisTemplate.delete(key);
        if (!functionKeys.isEmpty()) {
            stringRedisTemplate.opsForSet().add(key, functionKeys.toArray(new String[0]));
        }
    }

    public void refreshUsersByRoleKeys(Set<String> roleKeys) {
        if (roleKeys == null || roleKeys.isEmpty()) {
            return;
        }
        Set<Long> userIds = new HashSet<>();
        for (String roleKey : roleKeys) {
            Set<String> members = stringRedisTemplate.opsForSet().members(roleUsersKey(roleKey));
            if (members != null) {
                userIds.addAll(members.stream().map(Long::valueOf).collect(Collectors.toSet()));
            }
        }
        for (Long userId : userIds) {
            refreshUserPermissions(userId);
        }
    }

    public Set<String> getRoleAndParentKeys(SysRole role) {
        if (role == null) {
            return Collections.emptySet();
        }
        Map<Long, SysRole> roleMap = sysRoleService.list().stream()
            .collect(Collectors.toMap(SysRole::getId, r -> r));
        Set<String> keys = new HashSet<>();
        SysRole current = role;
        while (current != null) {
            keys.add(current.getRoleKey());
            if (current.getParentId() == null) {
                break;
            }
            current = roleMap.get(current.getParentId());
        }
        return keys;
    }

    private Map<Long, SysRoleDTO> buildRoleFunctionMap() {
        HashMap<Long, SysRoleDTO> roleMap = new HashMap<>();
        HashMap<Long, SysFunctionDTO> funcMap = new HashMap<>();
        List<SysRole> roles = sysRoleService.list();
        for (SysRole role : roles) {
            SysRoleDTO dto = BeanUtil.copyProperties(role, SysRoleDTO.class);
            roleMap.put(role.getId(), dto);
        }
        List<SysFunction> functions = sysFunctionService.list();
        for (SysFunction function : functions) {
            SysFunctionDTO dto = BeanUtil.copyProperties(function, SysFunctionDTO.class);
            funcMap.put(function.getId(), dto);
        }
        List<SysRoleFunction> sysRoleFunctions = sysFunctionService.getAllRoleFunctions();
        for (SysRoleFunction roleFunction : sysRoleFunctions) {
            SysRoleDTO roleDTO = roleMap.get(roleFunction.getRoleId());
            SysFunctionDTO funcDTO = funcMap.get(roleFunction.getFunctionId());
            if (roleDTO != null && funcDTO != null) {
                roleDTO.getFunctions().put(funcDTO.getFunctionKey(), funcDTO);
            }
        }
        for (SysRoleDTO role : roleMap.values()) {
            if (role.getParentId() != null) {
                collectParentFunctions(roleMap, role);
            }
        }
        return roleMap;
    }

    private void collectParentFunctions(Map<Long, SysRoleDTO> roleMap, SysRoleDTO role) {
        SysRoleDTO parentRole = roleMap.get(role.getParentId());
        if (parentRole == null) {
            return;
        }
        parentRole.getFunctions().putAll(role.getFunctions());
        if (parentRole.getParentId() != null) {
            collectParentFunctions(roleMap, parentRole);
        }
    }

    private String userFuncKey(Long userId) {
        return String.format(USER_FUNC_KEY_PATTERN, userId);
    }

    private String roleUsersKey(String roleKey) {
        return String.format(ROLE_USERS_KEY_PATTERN, roleKey);
    }
}
