package top.twindworld.treeauthority.demos.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.ibatis.mapping.FetchType;

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

    /**
     * 父角色 (对应数据库 parent_id)
     */
    private Long parentId;

}