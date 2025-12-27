package top.twindworld.treeauthority.demos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.twindworld.treeauthority.demos.domain.po.SysRole;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisAuthorityServiceTest {

    @Mock
    private SysRoleService sysRoleService;

    @Mock
    private SysFunctionService sysFunctionService;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

    private RedisAuthorityService redisAuthorityService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
        redisAuthorityService = new RedisAuthorityService(sysRoleService, sysFunctionService, stringRedisTemplate);
    }

    @Test
    void getUserFunctionKeysLoadsRolesOnCacheMiss() {
        Long userId = 1L;
        when(setOperations.members("user:1:roles")).thenReturn(Collections.emptySet());
        SysRole role = new SysRole();
        role.setId(2L);
        role.setRoleKey("admin");
        when(sysRoleService.getRolesByUserId(userId)).thenReturn(List.of(role));
        when(setOperations.members("role:admin:funcs")).thenReturn(Set.of("user:add", "user:manage"));

        Set<String> functionKeys = redisAuthorityService.getUserFunctionKeys(userId);

        assertThat(functionKeys).containsExactlyInAnyOrder("user:add", "user:manage");
        verify(setOperations).add("user:1:roles", "admin");
    }
}
