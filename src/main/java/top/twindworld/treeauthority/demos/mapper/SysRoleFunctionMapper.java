package top.twindworld.treeauthority.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface SysRoleFunctionMapper {

    @Select("SELECT COUNT(1) FROM sys_role_function WHERE role_id = #{roleId} AND function_id = #{functionId}")
    int countByRoleAndFunction(@Param("roleId") Long roleId, @Param("functionId") Long functionId);

    @Insert("INSERT INTO sys_role_function(role_id, function_id) VALUES(#{roleId}, #{functionId})")
    int insertRoleFunction(@Param("roleId") Long roleId, @Param("functionId") Long functionId);

    @Delete("DELETE FROM sys_role_function WHERE role_id = #{roleId} AND function_id = #{functionId}")
    int deleteRoleFunction(@Param("roleId") Long roleId, @Param("functionId") Long functionId);
}
