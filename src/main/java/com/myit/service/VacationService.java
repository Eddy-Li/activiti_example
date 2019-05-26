package com.myit.service;

import com.myit.controller.vo.VacationFormVO;

import java.io.InputStream;

public interface VacationService {

    void startVacationProcess(VacationFormVO vacationFormVO);

    InputStream getDiagram(String processInstanceId);
}
