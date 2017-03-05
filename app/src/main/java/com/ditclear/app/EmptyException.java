package com.ditclear.app;

/**
 * 页面描述：异常
 * <p>
 * Created by ditclear on 2017/3/5.
 */
public class EmptyException extends Exception {

    private int code;

    public EmptyException(int code) {
        super();
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
