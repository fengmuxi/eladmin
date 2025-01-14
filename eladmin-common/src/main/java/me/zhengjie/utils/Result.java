package me.zhengjie.utils;

import lombok.Data;
import me.zhengjie.utils.enums.ErrorEnum;

import java.io.Serializable;

import static me.zhengjie.utils.enums.ErrorEnum.SYSTEM_RUNTIME_ERROR;

/**
 * 统一返回对象
 */

@Data
public class Result<T> implements Serializable {
    /**
     * 通信数据
     */
    private T data;
    /**
     * 通信状态
     */
    private boolean flag = true;
    /**
     * 通信描述
     */
    private String msg = "操作成功";

    /**
     * 通过静态方法获取实例
     */
    public static <T> Result<T> of(T data) {
        return new Result<>(data);
    }
    public static <T> Result<T> of() {
        return new Result<>("操作成功！");
    }

    public static <T> Result<T> of(String msg) {
        return new Result<>(msg);
    }
    public static <T> Result<T> of(String msg, boolean flag) {
        return new Result<>(msg, flag);
    }

    public static <T> Result<T> of(T data, boolean flag, String msg) {
        return new Result<>(data, flag, msg);
    }

    public static <T> Result<T> error(ErrorEnum errorEnum) {
        return new Result(errorEnum.getCode(), false, errorEnum.getMsg());
    }

    public static <T> Result<T> error(String msg) {
        return new Result("502",false,msg);
    }

    public static <T> Result<T> error() {
        return new Result(SYSTEM_RUNTIME_ERROR,false,"操作失败！");
    }

    @Deprecated
    public Result() {

    }

    private Result(T data) {
        this.data = data;
    }

    private Result(String msg) {
        this.msg = msg;
    }


    private Result(String msg, boolean flag) {
        this.msg = msg;
        this.flag = flag;
    }

    private Result(T data, boolean flag, String msg) {
        this.data = data;
        this.flag = flag;
        this.msg = msg;
    }

}
