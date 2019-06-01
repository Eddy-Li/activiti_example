package com.myit.controller;

import com.myit.dao.VacationDao;
import com.myit.model.VacationDTO;
import com.myit.utils.ResultCode;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
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
    @RequestMapping(value = "/queryHistoryApplyProcess.do", method = RequestMethod.GET)
    public Map<String, Object> queryHistoryApplyProcess(@RequestParam("userId") String userId) {
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
        result.put("code", ResultCode.SUCCESS.getCode());
        result.put("msg", ResultCode.SUCCESS.getMsg());

        return result;
    }

    //查询结束的流程中任务清单，可以用来作为显示结束的流程的审批流程和审批内容
    //PS:如果查询当前正在执行的审批信息，可以使用此接口，然后再加上当前到到哪个节点信息就可以( List<Task> taskList = this.taskService.createTaskQuery().taskAssignee(userId).list();)
    @RequestMapping(value = "/queryHistoryTasksByProcessInstanceId.do", method = RequestMethod.GET)
    public Map<String, Object> queryHistoryTasksByProcessInstanceId(@RequestParam("processInstanceId") String processInstanceId) {
        Map<String, Object> result = new HashMap<>();

        if (StringUtils.isAnyBlank(processInstanceId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        List<HistoricTaskInstance> list = this.historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime()
                .asc()
                .list();
        for (HistoricTaskInstance task : list) {
//            Map<String, Object> processVariables = task.getProcessVariables();
            List<Comment> taskComments1 = taskService.getTaskComments(task.getId(), "opinion");
            List<Comment> taskComments2 = taskService.getTaskComments(task.getId(), "comment");
            List<Comment> taskComments3 = taskService.getProcessInstanceComments(processInstanceId);
            System.out.println(taskComments3);
        }

        result.put("data", list);
        result.put("code", ResultCode.SUCCESS.getCode());
        result.put("msg", ResultCode.SUCCESS.getMsg());

        return result;
    }


    //查询历史审批过的任务,然后可以获得实例id，进而可以获取该流程实例所有审批信息
    @RequestMapping(value = "/queryApprovalTaskList.do", method = RequestMethod.GET)
    public Map<String, Object> queryApprovalTaskList(@RequestParam("userId") String userId) {
        Map<String, Object> result = new HashMap<>();

        if (StringUtils.isAnyBlank(userId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        List<HistoricTaskInstance> list = this.historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).list();
        for (HistoricTaskInstance taskInstance : list) {
            System.out.println(taskInstance.getId());
            System.out.println(taskInstance.getProcessInstanceId());
            System.out.println(taskInstance.getProcessVariables());
        }

        result.put("data", list);
        result.put("code", ResultCode.SUCCESS.getCode());
        result.put("msg", ResultCode.SUCCESS.getMsg());

        return result;
    }

}
