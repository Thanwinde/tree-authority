package top.twindworld.treeauthority.demos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.twindworld.treeauthority.demos.domain.Result;
import top.twindworld.treeauthority.demos.domain.dto.RolePermissionRequest;
import top.twindworld.treeauthority.demos.service.RolePermissionService;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @PostMapping("/{roleKey}/permissions")
    public Result<String> addPermissions(@PathVariable String roleKey,
                                         @RequestBody RolePermissionRequest request) {
        rolePermissionService.addPermissions(roleKey, request.getFunctionKeys());
        return Result.success("角色权限已更新");
    }

    @DeleteMapping("/{roleKey}/permissions")
    public Result<String> removePermissions(@PathVariable String roleKey,
                                            @RequestBody RolePermissionRequest request) {
        rolePermissionService.removePermissions(roleKey, request.getFunctionKeys());
        return Result.success("角色权限已更新");
    }
}
