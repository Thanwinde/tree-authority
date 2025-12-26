package top.twindworld.treeauthority.demos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.twindworld.treeauthority.demos.domain.po.SysRole;
import top.twindworld.treeauthority.demos.domain.po.SysUserRole;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
    List<SysRole> getRolesByUserId(Long userId);

    List<SysUserRole> getAllUserRoles();
}
