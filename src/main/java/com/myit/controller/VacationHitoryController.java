package com.myit.controller;

import com.myit.dao.VacationDao;
import com.myit.model.VacationDTO;
import com.myit.utils.ResultCode;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/VacationHitoryController")
public class VacationHitoryController {

    @Autowired
    private VacationDao vacationDao;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private HistoryService historyService;


    //历史申请单：方法1：直接从OA_VACATION表里取，这个表需要加上标志位字段表示有没有结束
    //  或者弄个轨迹表；还需要加个字段表示最终同意还是不同意
    //方法2：先从OA_VACATION表里取processInstanceId，然后再用historyService取里面有数据的
    //这里用的方法2
    @RequestMapping(value = "/queryHitoryApplyProcess.do", method = RequestMethod.GET)
    public Map<String, Object> queryHitoryApplyProcess(@RequestParam("userId") String userId) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isAnyBlank(userId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        List<HistoricProcessInstance> list = new ArrayList<>();

        List<VacationDTO> vacationDTOList = vacationDao.queryApplyVacationDTO(userId);
        for (VacationDTO vacationDTO : vacationDTOList) {
            HistoricProcessInstance historicProcessInstance = this.historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(vacationDTO.getProcInstId())
                    .singleResult();
            if (historicProcessInstance != null) {
                list.add(historicProcessInstance);
                System.out.println(historicProcessInstance.getId());
                System.out.println(historicProcessInstance.getDeleteReason());
                System.out.println(historicProcessInstance);
            }
        }
        result.put("data", list);
        return result;
    }


}
