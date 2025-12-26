package top.twindworld.treeauthority.demos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.twindworld.treeauthority.demos.domain.po.SysFunction;
import top.twindworld.treeauthority.demos.domain.po.SysRoleFunction;

import java.util.List;

@Mapper
public interface SysFunctionMapper extends BaseMapper<SysFunction> {
    List<SysFunction> getFunctionsByRoleId(Long id);

    List<SysRoleFunction> getAllRoleFunctions();
}
