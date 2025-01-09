package com.cczj.urlservice.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.cczj.common.base.BaseConstant;
import com.cczj.common.dto.LogonUser;
import com.cczj.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        list.add("*");
        return list;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Object sessionInfo = StpUtil.getSessionByLoginId(loginId).get(BaseConstant.CONTEXT_LOGIN_USERNAME);
//        log.info("角色校验:{}", sessionInfo);
        if (sessionInfo != null) {
            LogonUser logonUser = JsonUtil.strToObj(sessionInfo.toString(), LogonUser.class);
            if (logonUser != null) {
                return Collections.singletonList(logonUser.getRoleId() + "");
            }
        }
        return new ArrayList<>();
    }


}
