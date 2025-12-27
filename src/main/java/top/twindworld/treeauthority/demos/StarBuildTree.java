package top.twindworld.treeauthority.demos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.twindworld.treeauthority.demos.service.RedisAuthorityService;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class StarBuildTree {
    private final RedisAuthorityService redisAuthorityService;
    @PostConstruct
    private void init(){
        redisAuthorityService.refreshAll();
        System.out.println("Redis 权限缓存初始化完成");
    }
}
