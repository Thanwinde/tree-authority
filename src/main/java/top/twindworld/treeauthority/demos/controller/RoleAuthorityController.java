package top.twindworld.treeauthority.demos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.twindworld.treeauthority.demos.domain.Result;
import top.twindworld.treeauthority.demos.service.RedisAuthorityService;

@RestController
@RequiredArgsConstructor
public class RoleAuthorityController {
    private final RedisAuthorityService redisAuthorityService;

    @PostMapping("/roles/{roleKey}/functions/{functionKey}")
    public Result<String> addRoleFunction(@PathVariable String roleKey, @PathVariable String functionKey) {
        boolean updated = redisAuthorityService.addRoleFunction(roleKey, functionKey);
        if (!updated) {
            return Result.error(404, "角色或功能不存在");
        }
        return Result.success("角色功能已更新");
    }

    @DeleteMapping("/roles/{roleKey}/functions/{functionKey}")
    public Result<String> removeRoleFunction(@PathVariable String roleKey, @PathVariable String functionKey) {
        boolean updated = redisAuthorityService.removeRoleFunction(roleKey, functionKey);
        if (!updated) {
            return Result.error(404, "角色或功能不存在");
        }
        return Result.success("角色功能已更新");
    }
}
