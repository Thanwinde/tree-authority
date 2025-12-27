package top.twindworld.treeauthority.demos.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionRequest {
    private List<String> functionKeys;
}
