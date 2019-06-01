package com.myit.module.activiti;

import com.myit.model.VacationDTO;
import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.User;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class VacationManagerTaskListener implements TaskListener {

    private static final long serialVersionUID = -1852289294675903686L;

    @Override
    public void notify(DelegateTask delegateTask) {
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        //IdentityService
        IdentityService identityService = context.getBean(IdentityService.class);
        TaskService taskService = context.getBean(TaskService.class);

        //获取流程参数
        VacationDTO form = (VacationDTO) delegateTask.getVariable("form");
        //获取请假的人id
        String userId = form.getUserId();
        //获取该员工的经理
        User manager = identityService.createUserQuery().memberOfGroup("G0000").singleResult();
        //设置该经理为经理审批的办理人,必须使用taskService.setAssignee()方法,不能使用task.setAssignee()方法
        taskService.setAssignee(delegateTask.getId(), manager.getId());
    }

}
