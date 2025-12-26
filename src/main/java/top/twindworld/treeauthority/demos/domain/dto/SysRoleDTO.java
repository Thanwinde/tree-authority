package top.twindworld.treeauthority.demos.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 @Getter @Setter 而不是 @Data，
 * 防止 JPA 双向关联时 hashCode() 死循环
 */
@Data
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roleName;

    private String roleKey;

    // ================== 1. 角色继承关系 (自关联) ==================

    /**
     * 父角色 (对应数据库 parent_id)
     */
    private SysRole parent;

    /**
     * 子角色列表 (可选，用于查询下级)
     */
    private List<SysRole> children = new ArrayList<>();

    // ================== 2. 角色-功能关系 (多对多) ==================

    /**
     * 该角色拥有的功能
     * 对应中间表：sys_role_function
     */
    private List<SysFunction> functions = new ArrayList<>();

    // ================== 3. 角色-用户关系 (多对多) ==================
    
    // 通常由 User 端维护关系，这里用 mappedBy 放弃维护权
    private List<SysUser> users = new ArrayList<>();
}