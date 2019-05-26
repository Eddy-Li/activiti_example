package com.myit.controller;

import com.myit.controller.vo.ActGroupVO;
import com.myit.controller.vo.ActUserVO;
import com.myit.controller.vo.MemberShipVO;
import com.myit.utils.ResultCode;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/AdminController")
public class AdminController {

    @Autowired
    private IdentityService identityService;

    @RequestMapping(value = "/addUser.do", method = RequestMethod.POST)
    public Map<String, Object> addUser(@RequestBody ActUserVO actUserVO) {
        Map<String, Object> result = new HashMap<>();

        String userId = actUserVO.getId();
//        String firstName = actUserVO.getFirstName();
        String lastName = actUserVO.getLastName();
        String email = actUserVO.getEmail();
        String password = actUserVO.getPassword();

        if (StringUtils.isAnyBlank(lastName, email, password, userId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        UserQuery userQuery = identityService.createUserQuery().userId(userId);
        User singleResult = userQuery.singleResult();
        if (singleResult != null) {
            result.put("code", ResultCode.USER_ID_HAVE_EXIST.getCode());
            result.put("msg", ResultCode.USER_ID_HAVE_EXIST.getMsg());
            return result;
        }

//        String userId = UUID.randomUUID().toString();
        User user = identityService.newUser(userId);
        //如果需要系统自动生成id，需要执行user.setId(null);
//        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        try {
            identityService.saveUser(user);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }


    @RequestMapping(value = "/displayUsers.do", method = RequestMethod.GET)
    public Map<String, Object> displayUsers() {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = this.identityService.createUserQuery().orderByUserLastName().asc().list();

        result.put("code", ResultCode.SUCCESS.getCode());
        result.put("msg", ResultCode.SUCCESS.getMsg());
        result.put("data", userList);
        return result;
    }

    @RequestMapping(value = "/deleteUser.do", method = RequestMethod.GET)
    public Map<String, Object> deleteUser(@RequestParam("id") String id) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isAnyBlank(id)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }
        try {
            this.identityService.deleteUser(id);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            ex.printStackTrace();
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    @RequestMapping(value = "/addGroup.do", method = RequestMethod.POST)
    public Map<String, Object> addGroup(@RequestBody ActGroupVO actGroupVO) {
        Map<String, Object> result = new HashMap<>();

        String groupId = actGroupVO.getId();
        String type = actGroupVO.getType();
        String name = actGroupVO.getName();

        if (StringUtils.isAnyBlank(groupId, type, name)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        GroupQuery groupQuery = identityService.createGroupQuery().groupId(groupId);
        Group isEXISTGroupId = groupQuery.singleResult();
        if (isEXISTGroupId != null) {
            result.put("code", ResultCode.GROUP_ID_HAVE_EXIST.getCode());
            result.put("msg", ResultCode.GROUP_ID_HAVE_EXIST.getMsg());
            return result;
        }

//        String groupId = UUID.randomUUID().toString();
//        //如果需要系统自动生成id，需要执行group.setId(null);
        Group group = identityService.newGroup(groupId);
        group.setType(type);
        group.setName(name);

        try {
            identityService.saveGroup(group);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    @RequestMapping(value = "/deleteGroup.do", method = RequestMethod.GET)
    public Map<String, Object> deleteGroup(@RequestParam("id") String id) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isAnyBlank(id)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }
        try {
            this.identityService.deleteGroup(id);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            ex.printStackTrace();
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    @RequestMapping(value = "/createMemberShip.do", method = RequestMethod.POST)
    public Map<String, Object> createMemberShip(@RequestBody MemberShipVO memberShip) {
        Map<String, Object> result = new HashMap<>();

        String userId = memberShip.getUserId();
        String groupId = memberShip.getGroupId();

        if (StringUtils.isAnyBlank(groupId, userId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        User isEXISTMemberShip = this.identityService.createUserQuery()
                .memberOfGroup(groupId)
                .userId(userId)
                .singleResult();
        if (isEXISTMemberShip != null) {
            result.put("code", ResultCode.MEMBER_SHIP_HAVE_EXIST.getCode());
            result.put("msg", ResultCode.MEMBER_SHIP_HAVE_EXIST.getMsg());
            return result;
        }

        try {
            identityService.createMembership(userId, groupId);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    @RequestMapping(value = "/deleteMemberShip.do", method = RequestMethod.POST)
    public Map<String, Object> deleteMemberShip(@RequestBody MemberShipVO memberShip) {
        Map<String, Object> result = new HashMap<>();

        String userId = memberShip.getUserId();
        String groupId = memberShip.getGroupId();

        if (StringUtils.isAnyBlank(groupId, userId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        try {
            identityService.deleteMembership(userId, groupId);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    @RequestMapping(value = "/displayGroups.do", method = RequestMethod.GET)
    public Map<String, Object> displayGroups() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Group> list = this.identityService.createGroupQuery().orderByGroupName().asc().list();
            result.put("data", list);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }

    @RequestMapping(value = "/displayAllUsersOfGroup.do", method = RequestMethod.GET)
    public Map<String, Object> displayAllUsersOfGroup(@RequestParam("groupId") String groupId) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isAnyBlank(groupId)) {
            result.put("code", ResultCode.PARAM_ERROR.getCode());
            result.put("msg", ResultCode.PARAM_ERROR.getMsg());
            return result;
        }

        try {
            List<User> list = this.identityService
                    .createUserQuery()
                    .memberOfGroup(groupId)
                    .orderByUserLastName()
                    .asc()
                    .list();
            result.put("data", list);
            result.put("code", ResultCode.SUCCESS.getCode());
            result.put("msg", ResultCode.SUCCESS.getMsg());
        } catch (Exception ex) {
            result.put("code", ResultCode.EXCEPTION.getCode());
            result.put("msg", ResultCode.EXCEPTION.getMsg());
        }
        return result;
    }
}
