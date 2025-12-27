package top.twindworld.treeauthority.demos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.twindworld.treeauthority.demos.domain.po.SysFunction;
import top.twindworld.treeauthority.demos.domain.po.SysRoleFunction;

import java.util.List;

@Mapper
public interface SysFunctionMapper extends BaseMapper<SysFunction> {
    List<SysFunction> getFunctionsByRoleId(Long id);

    List<SysRoleFunction> getAllRoleFunctions();

    int insertRoleFunction(@Param("roleId") Long roleId, @Param("functionId") Long functionId);

    int deleteRoleFunction(@Param("roleId") Long roleId, @Param("functionId") Long functionId);
}
