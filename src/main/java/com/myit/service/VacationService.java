package com.myit.service;

import com.myit.controller.vo.VacationFormVO;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface VacationService {

    void startVacationProcess(VacationFormVO vacationFormVO);

    InputStream getDiagram(String processInstanceId);

    List<Map<String, Object>> queryApplyProcessList(String userId);
}
