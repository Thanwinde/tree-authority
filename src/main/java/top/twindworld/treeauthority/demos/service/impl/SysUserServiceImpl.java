package top.twindworld.treeauthority.demos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.twindworld.treeauthority.demos.domain.entity.SysUser;
import top.twindworld.treeauthority.demos.mapper.SysUserMapper;
import top.twindworld.treeauthority.demos.service.SysUserService;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
