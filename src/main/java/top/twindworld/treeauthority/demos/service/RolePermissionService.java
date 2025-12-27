package top.twindworld.treeauthority.demos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.twindworld.treeauthority.demos.aop.interceptor.NoAuthorityException;
import top.twindworld.treeauthority.demos.domain.po.SysFunction;
import top.twindworld.treeauthority.demos.domain.po.SysRole;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private final SysRoleService sysRoleService;
    private final SysFunctionService sysFunctionService;
    private final RedisAuthorityService redisAuthorityService;

    @Transactional
    public void addPermissions(String roleKey, List<String> functionKeys) {
        if (functionKeys == null || functionKeys.isEmpty()) {
            return;
        }
        SysRole role = sysRoleService.lambdaQuery()
            .eq(SysRole::getRoleKey, roleKey)
            .one();
        if (role == null) {
            throw new NoAuthorityException(404, "角色不存在: " + roleKey);
        }
        for (String functionKey : functionKeys) {
            SysFunction function = sysFunctionService.lambdaQuery()
                .eq(SysFunction::getFunctionKey, functionKey)
                .one();
            if (function == null) {
                throw new NoAuthorityException(404, "功能不存在: " + functionKey);
            }
            sysFunctionService.insertRoleFunction(role.getId(), function.getId());
        }
        redisAuthorityService.refreshRoleFunctions(role);
    }

    @Transactional
    public void removePermissions(String roleKey, List<String> functionKeys) {
        if (functionKeys == null || functionKeys.isEmpty()) {
            return;
        }
        SysRole role = sysRoleService.lambdaQuery()
            .eq(SysRole::getRoleKey, roleKey)
            .one();
        if (role == null) {
            throw new NoAuthorityException(404, "角色不存在: " + roleKey);
        }
        for (String functionKey : functionKeys) {
            SysFunction function = sysFunctionService.lambdaQuery()
                .eq(SysFunction::getFunctionKey, functionKey)
                .one();
            if (function == null) {
                continue;
            }
            sysFunctionService.deleteRoleFunction(role.getId(), function.getId());
        }
        redisAuthorityService.refreshRoleFunctions(role);
    }
}
