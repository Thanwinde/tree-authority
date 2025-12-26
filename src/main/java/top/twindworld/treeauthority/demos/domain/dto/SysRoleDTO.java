package top.twindworld.treeauthority.demos.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class SysRoleDTO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleName;

    private String roleKey;

    private Long parentId;
    /**
     * 父角色 (对应数据库 parent_id)
     */
    private SysRoleDTO parent;

    /**
     * 子角色列表 (可选，用于查询下级)
     */
    private List<SysRoleDTO> children = new ArrayList<>();

    // ================== 2. 角色-功能关系 (多对多) ==================

    /**
     * 该角色拥有的功能
     * 对应中间表：sys_role_function
     */
    private HashMap<String,SysFunctionDTO> functions = new HashMap<>();

}