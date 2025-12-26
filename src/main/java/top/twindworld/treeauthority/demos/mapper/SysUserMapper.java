package top.twindworld.treeauthority.demos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.twindworld.treeauthority.demos.domain.entity.SysUser;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}

