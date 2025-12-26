package top.twindworld.treeauthority.demos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.twindworld.treeauthority.demos.domain.po.SysRole;
import top.twindworld.treeauthority.demos.domain.po.SysUserRole;
import top.twindworld.treeauthority.demos.mapper.SysRoleMapper;
import top.twindworld.treeauthority.demos.service.SysRoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    private final SysRoleMapper sysRoleMapper;

    @Override
    public List<SysRole> getRolesByUserId(Long id) {
        return sysRoleMapper.getRolesByUserId(id);
    }

    @Override
    public List<SysUserRole> getAllUserRoles() {
        return sysRoleMapper.getAllUserRoles();
    }
}
