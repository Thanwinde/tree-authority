package top.twindworld.treeauthority.demos.config;

import top.twindworld.treeauthority.demos.domain.dto.SysFunctionDTO;
import top.twindworld.treeauthority.demos.domain.dto.SysRoleDTO;
import top.twindworld.treeauthority.demos.domain.dto.SysUserDTO;

import java.util.HashMap;

public class AuthorityCollection {
    public static HashMap<Long, SysUserDTO> userMap = new HashMap<>();
    public static HashMap<Long, SysRoleDTO> roleMap = new HashMap<>();
    public static HashMap<Long, SysFunctionDTO> funcMap = new HashMap<>();

    public static void refreshAll(HashMap<Long, SysUserDTO> users,
                               HashMap<Long, SysRoleDTO> roles,
                               HashMap<Long, SysFunctionDTO> funcRoles) {
        userMap = users;
        roleMap = roles;
        funcMap = funcRoles;
    }

}
