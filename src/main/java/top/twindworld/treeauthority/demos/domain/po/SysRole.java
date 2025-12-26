package top.twindworld.treeauthority.demos.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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

    /**
     * 父角色 (对应数据库 parent_id)
     */
    private Long parentId;

}