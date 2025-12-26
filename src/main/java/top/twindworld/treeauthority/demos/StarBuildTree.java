package top.twindworld.treeauthority.demos;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.twindworld.treeauthority.demos.config.AuthorityCollection;
import top.twindworld.treeauthority.demos.domain.dto.SysFunctionDTO;
import top.twindworld.treeauthority.demos.domain.dto.SysRoleDTO;
import top.twindworld.treeauthority.demos.domain.dto.SysUserDTO;
import top.twindworld.treeauthority.demos.domain.po.*;
import top.twindworld.treeauthority.demos.service.SysFunctionService;
import top.twindworld.treeauthority.demos.service.SysRoleService;
import top.twindworld.treeauthority.demos.service.SysUserService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StarBuildTree {
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysFunctionService sysFunctionService;

    private final HashMap<Long, SysUserDTO> userMap = new HashMap<>();
    private final HashMap<Long, SysRoleDTO> roleMap = new HashMap<>();
    private final HashMap<Long, SysFunctionDTO> funcMap = new HashMap<>();
    @PostConstruct
    private void init(){
        // 1. 加载所有用户
        List<SysUser> users = sysUserService.list();
        for (SysUser user : users){
            SysUserDTO dto = BeanUtil.copyProperties(user, SysUserDTO.class);
            userMap.put(user.getId(), dto);
        }

        // 2. 加载所有角色
        List<SysRole> roles = sysRoleService.list();
        for (SysRole role : roles){
            SysRoleDTO dto = BeanUtil.copyProperties(role, SysRoleDTO.class);
            roleMap.put(role.getId(), dto);
        }

        //3. 加载所有功能
        List<SysFunction> functions = sysFunctionService.list();
        for (SysFunction function : functions){
            SysFunctionDTO dto = BeanUtil.copyProperties(function, SysFunctionDTO.class);
            funcMap.put(function.getId(), dto);
        }

        // 4. 建立用户-角色关系
        List<SysUserRole> sysUserRoles = sysRoleService.getAllUserRoles();
        for (SysUserRole userRole : sysUserRoles){
            SysUserDTO userDTO = userMap.get(userRole.getUserId());
            SysRoleDTO roleDTO = roleMap.get(userRole.getRoleId());
            if (userDTO != null && roleDTO != null){
                userDTO.getRoles().add(roleDTO);
                userDTO.getRoleSet().add(roleDTO.getRoleKey());
            }
        }

        // 5. 建立角色-功能关系
        List<SysRoleFunction> sysRoleFunctions = sysFunctionService.getAllRoleFunctions();
        for (SysRoleFunction roleFunction : sysRoleFunctions){
            SysRoleDTO roleDTO = roleMap.get(roleFunction.getRoleId());
            SysFunctionDTO funcDTO = funcMap.get(roleFunction.getFunctionId());
            if (roleDTO != null && funcDTO != null) {
                roleDTO.getFunctions().put(funcDTO.getFunctionKey(), funcDTO);
            }
        }

        // 6. 建立角色继承关系
        for (SysRoleDTO role : roleMap.values()){
            if (role.getParentId() != null) {    // 有父角色
                collectParentFunctions(role);
            }
        }
        for(SysUserDTO user : userMap.values()){
            // 收集用户所有角色的功能
            for (SysRoleDTO role : user.getRoles()){
                for(SysFunctionDTO func : role.getFunctions().values()){
                    user.getFunctions().add(func);
                    user.getFunctionSet().add(func.getFunctionKey());
                }
            }
        }
        AuthorityCollection.refreshAll(userMap, roleMap, funcMap);
        System.out.println("权限树初始化完成");
    }
    private void collectParentFunctions(SysRoleDTO role){
        SysRoleDTO parentRole = roleMap.get(role.getParentId());
        parentRole.getFunctions().putAll(role.getFunctions());
        if (parentRole.getParentId() != null) { //还有父角色
            collectParentFunctions(parentRole);
        }

    }
}
