package com.myit.controller.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class ActUserVO implements Serializable {
    private static final long serialVersionUID = -8767548536843074952L;

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
