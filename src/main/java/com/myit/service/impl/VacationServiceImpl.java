package com.myit.service.impl;

import com.myit.controller.vo.VacationFormVO;
import com.myit.dao.VacationDao;
import com.myit.model.VacationDTO;
import com.myit.service.VacationService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class VacationServiceImpl implements VacationService {
    @Autowired
    private VacationDao vacationDao;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void startVacationProcess(VacationFormVO vacationFormVO) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(vacationFormVO.getProcessId());

        VacationDTO vacation = new VacationDTO();
        vacation.setWorkDays(Integer.valueOf(vacationFormVO.getWorkDays()));
        vacation.setBeginDate(LocalDateTime.parse(vacationFormVO.getBeginDate(), dateTimeFormatter));
        vacation.setEndDate(LocalDateTime.parse(vacationFormVO.getEndDate(), dateTimeFormatter));
        vacation.setVacType(Integer.valueOf(vacationFormVO.getVacType()));
        vacation.setReason(vacationFormVO.getReason());
        vacation.setUserId(vacationFormVO.getUserId());
        vacation.setProcInstId(processInstance.getId());

        Map<String, Object> vars = new HashMap<>();
        vars.put("days", Integer.valueOf(vacationFormVO.getWorkDays()));
        vars.put("form", vacation);

        //填写请假单任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        //设置任务代理人（受理人）
        taskService.setAssignee(task.getId(), vacationFormVO.getUserId());
        //完成任务，并将任务参数设置为流程参数，即全局参数
        taskService.complete(task.getId(), vars, false);

        this.vacationDao.insertVacationDTO(vacation);
    }
}