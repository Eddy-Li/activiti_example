package com.myit.utils;

public enum ResultCode {
    SUCCESS("00000000","成功"),
    EXCEPTION("99999999","系统异常"),
    PARAM_ERROR("88888888","参数错误"),
    USER_ID_HAVE_EXIST("00001001","用户id已经存在"),
    GROUP_ID_HAVE_EXIST("00001002","用户组id已经存在"),
    MEMBER_SHIP_HAVE_EXIST("00001003","用户已经在用户组中"),
    USER_NOT_EXIST("00001004","用户不存在")
    ;
    
    private String code;
    private String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
