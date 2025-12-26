package top.twindworld.treeauthority.demos.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Data
@TableName("sys_user")
public class SysUserDTO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private List<SysRoleDTO> roles = new ArrayList<>();

    private  HashSet<String> roleSet = new HashSet<>();

    private List<SysFunctionDTO> functions = new ArrayList<>();

    private HashSet<String> functionSet = new HashSet<>();
}