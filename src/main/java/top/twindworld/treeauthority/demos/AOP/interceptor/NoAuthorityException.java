package top.twindworld.treeauthority.demos.AOP.interceptor;

import lombok.Getter;

@Getter
public class NoAuthorityException extends RuntimeException {
    private Integer code;
    private String msg;

    // 默认使用 500 错误码
    public NoAuthorityException(String msg) {
        super(msg);
        this.code = 403;
        this.msg = msg;
    }

    // 自定义错误码
    public NoAuthorityException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}