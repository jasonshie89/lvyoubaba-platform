package org.example.apimanage.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一响应结果类
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    public R() {}

    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> R<T> ok() {
        return new R<>(200, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> R<T> error() {
        return new R<>(500, "操作失败", null);
    }

    /**
     * 失败响应（带消息）
     */
    public static <T> R<T> error(String message) {
        return new R<>(500, message, null);
    }

    /**
     * 失败响应（带状态码和消息）
     */
    public static <T> R<T> error(Integer code, String message) {
        return new R<>(code, message, null);
    }
}