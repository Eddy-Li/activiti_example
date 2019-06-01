package com.myit.controller.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


//申请流程时，用户填写的参数
@Setter
@Getter
public class VacationFormVO implements Serializable {
    private static final long serialVersionUID = -4258595287604324442L;

    //带薪假
    public final static int TYPE_PAID = 0;
    //病假
    public final static int TYPE_SICK = 1;
    //事假
    public final static int TYPE_MATTER = 2;


    //休假天数
    private String workDays;

    private String beginDate;

    private String endDate;

    private String vacType;

    private String reason;

    private String processId;

    private String userId;

    private String processInstanceId;

    //意见 1:同意 2:不同意
    private String opinion;
    //评论
    private String comment;

    private String taskId;

}
