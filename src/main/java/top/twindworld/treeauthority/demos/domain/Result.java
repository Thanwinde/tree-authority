package top.twindworld.treeauthority.demos.domain;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 状态码：200成功，500系统异常，403无权限
    private String msg;   // 提示信息
    private T data;       // 数据载体

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "操作成功";
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }
}