package top.twindworld.treeauthority.demos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.twindworld.treeauthority.demos.domain.po.SysFunction;
import top.twindworld.treeauthority.demos.domain.po.SysRoleFunction;
import top.twindworld.treeauthority.demos.mapper.SysFunctionMapper;
import top.twindworld.treeauthority.demos.service.SysFunctionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysFunctionServiceImpl extends ServiceImpl<SysFunctionMapper, SysFunction> implements SysFunctionService {
    private final SysFunctionMapper sysFunctionMapper;
    @Override
    public List<SysFunction> getFunctionsByRoleId(Long id) {
        return sysFunctionMapper.getFunctionsByRoleId(id);
    }

    @Override
    public List<SysRoleFunction> getAllRoleFunctions() {
        return sysFunctionMapper.getAllRoleFunctions();
    }

    @Override
    public int insertRoleFunction(Long roleId, Long functionId) {
        return sysFunctionMapper.insertRoleFunction(roleId, functionId);
    }

    @Override
    public int deleteRoleFunction(Long roleId, Long functionId) {
        return sysFunctionMapper.deleteRoleFunction(roleId, functionId);
    }
}
