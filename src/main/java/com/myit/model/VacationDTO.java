package com.myit.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class VacationDTO implements Serializable {
    private static final long serialVersionUID = 3350393626597910749L;

    //带薪假
    public final static int TYPE_PAID = 0;
    //病假
    public final static int TYPE_SICK = 1;
    //事假
    public final static int TYPE_MATTER = 2;

    private int id;

    //休假天数
    private Integer workDays;

    private LocalDateTime beginDate;

    private LocalDateTime endDate;

    private Integer vacType;

    private String reason;

    private String procInstId;

    private String userId;

}
