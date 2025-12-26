package top.twindworld.treeauthority.demos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.twindworld.treeauthority.demos.domain.dto.SysFunctionDTO;
import top.twindworld.treeauthority.demos.domain.entity.SysFunction;
import top.twindworld.treeauthority.demos.domain.entity.SysRoleFunction;

import java.util.List;

public interface SysFunctionService extends IService<SysFunction> {
    List<SysFunction> getFunctionsByRoleId(Long id);

    List<SysRoleFunction> getAllRoleFunctions();
}
