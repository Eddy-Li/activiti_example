package com.myit.controller;

import com.myit.controller.vo.VacationFormVO;
import com.myit.service.VacationService;
import com.myit.utils.ResultCode;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/VacationController")
public class VacationController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;

    @Autowired
    private VacationService vacationService;


    @RequestMapping("/getAllProcess.do")
    public Map<String, Object> getAllProcess() {
        Map<String, Object> result = new HashMap<>();

        try {
            ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
            List<ProcessDefinition> processList = query.active().latestVersion().orderByProcessDefinitionName().asc().list();
            List<Map<String, Object>> list = new ArrayList<>();
            if (processList != null && processList.size() > 0) {
                for (ProcessDefinition processDefinition : processList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", processDefinition.getId());
                    map.put("key", processDefinition.getKey());
                    map.put("name", processDefinition.getName());
                    list.add(map);
                }
            }
            result.put("data", list);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    @RequestMapping("/startVacationProcess.do")
    public Map<String, Object> startVacationProcess(@RequestBody VacationFormVO vacationFormVO) {
        Map<String, Object> result = new HashMap<>();

        String processId = vacationFormVO.getProcessId();
        String beginDateStr = vacationFormVO.getBeginDate();
        String endDateStr = vacationFormVO.getEndDate();
        String workDays = vacationFormVO.getWorkDays();
        String vacType = vacationFormVO.getVacType();
        String reason = vacationFormVO.getReason();
        String userId = vacationFormVO.getUserId();

        if (StringUtils.isAnyBlank(processId, beginDateStr, endDateStr, workDays, vacType, reason, userId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        User user = this.identityService.createUserQuery().userId(userId).singleResult();
        if (user == null) {
            result.put("code", ResultCode.USER_NOT_EXIST.getCode());
            result.put("msg", ResultCode.USER_NOT_EXIST.getMsg());
            return result;
        }

        try {
            vacationService.startVacationProcess(vacationFormVO);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }


}