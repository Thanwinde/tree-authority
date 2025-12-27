# 一个简单的基于RBAC1的鉴权系统
采用Redis存储user->roles和role->functions的映射关系
只是一个简单的示例，大部分由codex生成，仅供参考
框架和思路参考我的博客：http://www.twindworld.top/blog/%E6%8A%80%E6%9C%AF/RBAC%E4%BB%8B%E7%BB%8D%E4%BB%A5%E5%8F%8A%E5%A6%82%E4%BD%95%E8%AE%BE%E8%AE%A1%E4%B8%80%E4%B8%AA%E7%AE%80%E6%98%93%E4%B8%94%E9%AB%98%E5%8F%AF%E7%94%A8%E7%9A%84RBAC1%E7%9A%84%E9%89%B4%E6%9D%83%E7%B3%BB%E7%BB%9F


SQL如下：

```
create table sys_function
(
id            bigint auto_increment primary key,
function_name varchar(256) null comment '功能名称',
function_key  varchar(256) null comment '功能标识符，如 user:add'
);

create table sys_role
(
id        bigint auto_increment primary key,
role_name varchar(256) null comment '角色名称',
role_key  varchar(256) null comment '角色标识',
parent_id bigint       null comment '父角色ID，实现角色继承'
);

create table sys_user
(
id       bigint auto_increment primary key,
username varchar(256) null,
password varchar(256) null
);

create table sys_user_role
(
user_id bigint not null,
role_id bigint not null,
primary key (user_id, role_id)
);

create table sys_role_function
(
role_id     bigint not null,
function_id bigint not null,
primary key (role_id, function_id)
); 
```