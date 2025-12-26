package top.twindworld.treeauthority.demos.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_function")
public class SysFunctionDTO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 功能名称，如：添加用户
     */
    private String functionName;

    /**
     * 功能标识符，如：user:add
     */
    private String functionKey;
}