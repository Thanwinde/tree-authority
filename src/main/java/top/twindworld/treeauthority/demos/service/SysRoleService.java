package top.twindworld.treeauthority.demos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.twindworld.treeauthority.demos.domain.dto.SysRoleDTO;
import top.twindworld.treeauthority.demos.domain.entity.SysRole;
import top.twindworld.treeauthority.demos.domain.entity.SysUser;
import top.twindworld.treeauthority.demos.domain.entity.SysUserRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {


    List<SysRole> getRolesByUserId(Long id);

    List<SysUserRole> getAllUserRoles();
}
