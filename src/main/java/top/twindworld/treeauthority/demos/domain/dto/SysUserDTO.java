package top.twindworld.treeauthority.demos.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /**
     * 密码通常不返回给前端，加上 @JsonIgnore
     */
    private String password;

    // ================== 用户-角色关系 (多对多) ==================

    /**
     * 用户拥有的角色
     * 对应中间表：sys_user_role
     */
    private List<SysRole> roles = new ArrayList<>();
}