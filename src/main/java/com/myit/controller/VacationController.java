package com.myit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(VacationController.class);

    //获取所有流程定义
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

    //启动一个流程实例
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

    //显示流程实例的流程图、及当前节点高亮显示
    @RequestMapping(value = "/showDiagram.do", method = RequestMethod.GET)
    public String showDiagram(HttpServletResponse response,
                              @RequestParam("processInstanceId") String processInstanceId) {

        OutputStream outputStream = null;
        try {
            InputStream inputStream = this.vacationService.getDiagram(processInstanceId);
            //response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE + ";" + "charset=UTF-8");
            response.setContentType("multipart/form-data;charset=UTF-8");
            outputStream = response.getOutputStream();
            outputStream.write(this.getImgByte(inputStream));
            outputStream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private byte[] getImgByte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int b;
        while ((b = inputStream.read()) != -1) {
            byteArrayOutputStream.write(b);
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return bytes;
    }

    //根据用户id，获取申请的流程列表
    @RequestMapping(value = "/queryApplyProcessList.do", method = RequestMethod.POST)
    public Map<String, Object> queryApplyProcessList(@RequestBody VacationFormVO vacationFormVO) {
        Map<String, Object> result = new HashMap<>();

        String userId = vacationFormVO.getUserId();

        if (StringUtils.isAnyBlank(userId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }
        try {
            List<Map<String, Object>> data = this.vacationService.queryApplyProcessList(userId);
            result.put("data", data);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getMessage());
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    //根据用户id，获取待审批的流程列表
    @RequestMapping(value = "/queryToApprovalProcessList.do", method = RequestMethod.POST)
    public Map<String, Object> queryToApprovalProcessList(@RequestBody VacationFormVO vacationFormVO) {
        Map<String, Object> result = new HashMap<>();

        String userId = vacationFormVO.getUserId();

        if (StringUtils.isAnyBlank(userId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }
        try {
            List<Map<String, Object>> data = this.vacationService.queryToApprovalProcessList(userId);
            result.put("data", data);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getMessage());
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    //审批流程
    @RequestMapping(value = "/approvalProcess.do", method = RequestMethod.POST)
    public Map<String, Object> approvalProcess(@RequestBody VacationFormVO vacationFormVO) {
        Map<String, Object> result = new HashMap<>();

        String userId = vacationFormVO.getUserId();
        String taskId = vacationFormVO.getTaskId();
        String opinion = vacationFormVO.getOpinion();
        String comment = vacationFormVO.getComment();
        String processInstanceId = vacationFormVO.getProcessInstanceId();

        if (StringUtils.isAnyBlank(userId, taskId, opinion, comment)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }
        try {
            this.vacationService.approvalProcess(userId, taskId, processInstanceId, opinion, comment);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getMessage());
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }


}
