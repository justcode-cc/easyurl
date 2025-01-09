package com.cczj.urlservice.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cczj.common.base.*;
import com.cczj.common.bean.params.LogonParams;
import com.cczj.common.bean.params.sys.SysUserUpdatePwdParams;
import com.cczj.common.dto.LogonUser;
import com.cczj.common.dto.SysUserJobDTO;
import com.cczj.common.entity.sys.SysMenuEntity;
import com.cczj.common.entity.sys.SysRoleMenuEntity;
import com.cczj.common.entity.sys.SysUserEntity;
import com.cczj.common.entity.sys.SysUserJobEntity;
import com.cczj.common.enums.sys.SysUserStatusEnum;
import com.cczj.common.utils.CryptoUtils;
import com.cczj.common.utils.JsonUtil;
import com.cczj.common.vo.LogonKeyIvVo;
import com.cczj.common.vo.MetaVo;
import com.cczj.common.vo.RouterVo;
import com.cczj.common.vo.SysMenuVo;
import com.cczj.framework.utils.IpUtils;
import com.cczj.framework.utils.LogonUtils;
import com.cczj.urlservice.mapper.sys.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseService {


    private final SysUserMapper sysUserMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserJobMapper sysUserJobMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final RedissonClient redissonClient;

    public R<LogonKeyIvVo> getKeyIv(HttpServletRequest request) {
        //限制同一ip每小时请求次数 比如每小时上限60次
        String ipAddress = IpUtils.getIpAddress(request);
        RBucket<Object> bucket = this.redissonClient.getBucket(RedisConstant.logon_max_limit() + ipAddress);
        int max = 0;
        if (bucket.isExists()) {
            max = Integer.parseInt(bucket.get().toString());
            if (max >= 60) {
                return R.fail("检测到您当前登录环境异常,请您稍后再试");
            } else {
                max++;
            }
        } else {
            max++;
        }
        String key = RandomUtil.randomString(16);
        String iv = RandomUtil.randomString(16);
        bucket = this.redissonClient.getBucket(RedisConstant.logon_max_limit() + ipAddress);
        bucket.set(max, bucket.remainTimeToLive() < 0 ? 60 : bucket.remainTimeToLive(), TimeUnit.MINUTES);
        this.redissonClient.getBucket(RedisConstant.logon_key_iv() + key).set(iv, 180, TimeUnit.SECONDS);
        LogonKeyIvVo logonKeyIvVo = new LogonKeyIvVo();
        logonKeyIvVo.setIv(iv);
        logonKeyIvVo.setKey(key);
        return R.success(logonKeyIvVo);
    }


    public R<LogonUser> logon(LogonParams params) {
        SysUserEntity sysUser = this.sysUserMapper.selectOne(Wrappers.lambdaQuery(SysUserEntity.class)
                .eq(SysUserEntity::getAccount, params.getAccount())
                .eq(SysUserEntity::getDeleted, 0));
        if (sysUser == null) {
            return R.fail("账号或密码错误!");
        }
        RBucket<Object> bucket = this.redissonClient.getBucket(RedisConstant.logon_key_iv() + params.getKey());
        if (!bucket.isExists()) {
            return R.fail("账号或密码错误~");
        }
        try {
            params.setPassword(CryptoUtils.aesDecrypt(params.getPassword(), params.getKey(), bucket.get().toString()));
        } catch (Exception e) {
            log.error("解密密码失败", e);
            return R.fail("账号或密码错误~~");
        }
        String pwd = DigestUtil.md5Hex(params.getPassword() + sysUser.getSalt());
        if (!sysUser.getPassword().equals(pwd)) {
            return R.fail("账号或密码错误");
        }
        if (!SysUserStatusEnum.normal.getValue().equals(sysUser.getStatus())) {
            return R.fail("账号状态异常，请联系管理员");
        }
        LogonUser logonUser = new LogonUser();
        logonUser.setUserId(sysUser.getId());
        logonUser.setAccount(params.getAccount());
        logonUser.setUsername(sysUser.getUsername());
        List<SysUserJobEntity> userJobList = this.sysUserJobMapper.selectList(Wrappers.lambdaQuery(SysUserJobEntity.class)
                .eq(SysUserJobEntity::getUserId, sysUser.getId())
                .eq(SysUserJobEntity::getDeleted, 0));
        logonUser.setJobId(userJobList.get(0).getId());
        logonUser.setRoleId(userJobList.get(0).getRoleId());
        logonUser.setJobList(userJobList.stream().map(v -> {
            SysUserJobDTO dto = new SysUserJobDTO();
            dto.setId(v.getId());
            dto.setRoleId(v.getRoleId());
            dto.setRoleName(this.sysRoleMapper.selectById(v.getRoleId()).getRoleName());
            dto.setDeptId(v.getDeptId());
            dto.setDeptAdmin(v.getDeptAdmin());
            return dto;
        }).collect(Collectors.toList()));
        StpUtil.login(logonUser.getJobId());
        String tokenValue = StpUtil.getTokenValue();
        logonUser.setToken(tokenValue);
        StpUtil.getSession().set(BaseConstant.CONTEXT_LOGIN_USERNAME, JsonUtil.toJsonString(logonUser));
        ContextHolder.setCurrentUser(JsonUtil.toJsonString(logonUser));
        return R.success(logonUser);
    }

    public R<LogonUser> userInfo() {
        LogonUser currentUser = ContextHolder.getCurrentUser(LogonUser.class);
        return R.success(currentUser);
    }

    public R<List<RouterVo>> getRouters() {
        LogonUser currentUser = ContextHolder.getCurrentUser(LogonUser.class);
        List<SysMenuEntity> sysMenuList;
        if (LogonUtils.isAdmin(currentUser)) {
            sysMenuList = this.sysMenuMapper.selectList(Wrappers.lambdaQuery(SysMenuEntity.class)
                    .eq(SysMenuEntity::getDeleted, 0)
                    .orderByAsc(SysMenuEntity::getParentId, SysMenuEntity::getOrderNum));
        } else {
            List<SysRoleMenuEntity> roleMenuEntityList = this.sysRoleMenuMapper.selectList(Wrappers.lambdaQuery(SysRoleMenuEntity.class)
                    .eq(SysRoleMenuEntity::getRoleId, currentUser.getRoleId())
                    .eq(SysRoleMenuEntity::getDeleted, 0));
            sysMenuList = this.sysMenuMapper.selectList(Wrappers.lambdaQuery(SysMenuEntity.class)
                    .eq(SysMenuEntity::getDeleted, 0)
                    .in(SysMenuEntity::getId, roleMenuEntityList.stream().map(SysRoleMenuEntity::getMenuId).collect(Collectors.toList()))
                    .orderByAsc(SysMenuEntity::getParentId, SysMenuEntity::getOrderNum));
        }
        List<SysMenuVo> child = this.getChild(sysMenuList, 0L);
        List<RouterVo> routers = this.getRouters(child);
        return R.success(routers);
    }

    private List<SysMenuVo> getChild(List<SysMenuEntity> sysMenuList, Long parentId) {
        List<SysMenuVo> vo = new ArrayList<>();
        for (SysMenuEntity sysMenu : sysMenuList) {
            if (parentId.equals(sysMenu.getParentId())) {
                SysMenuVo parentVo = new SysMenuVo();
                BeanUtils.copyProperties(sysMenu, parentVo);
                this.recursionTree(sysMenuList, sysMenu, parentVo);
                vo.add(parentVo);
            }
        }
        return vo;
    }

    private void recursionTree(List<SysMenuEntity> sysMenuEntityList, SysMenuEntity parent, SysMenuVo parentVo) {
        for (SysMenuEntity sysMenu : sysMenuEntityList) {
            if (sysMenu.getParentId().equals(parent.getId())) {
                List<SysMenuVo> children = parentVo.getChildren();
                SysMenuVo child = new SysMenuVo();
                BeanUtils.copyProperties(sysMenu, child);
                if (!CollUtil.isNotEmpty(children)) {
                    children = new ArrayList<>();
                    parentVo.setChildren(children);
                }
                children.add(child);
                recursionTree(sysMenuEntityList, sysMenu, child);
            }
        }
    }


    private boolean isMenuFrame(SysMenuVo menu) {
        return menu.getParentId().intValue() == 0 && MenuConstant.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame() == 0;
    }

    private String getRouteName(SysMenuVo menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    public boolean isInnerLink(SysMenuVo menu) {
        return menu.getIsFrame() == 0 && menu.getPath().startsWith("http");
    }

    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{"http://", "https://", "www.", ".", ":"},
                new String[]{"", "", "", "/", "/"});
    }

    public String getRouterPath(SysMenuVo menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && MenuConstant.TYPE_DIR.equals(menu.getMenuType())
                && menu.getIsFrame() == 0) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    public boolean isParentView(SysMenuVo menu) {
        return menu.getParentId().intValue() != 0 && MenuConstant.TYPE_DIR.equals(menu.getMenuType());
    }

    public String getComponent(SysMenuVo menu) {
        String component = MenuConstant.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = MenuConstant.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = MenuConstant.PARENT_VIEW;
        }
        return component;
    }

    private List<RouterVo> getRouters(List<SysMenuVo> sysMenuList) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenuVo menu : sysMenuList) {
            RouterVo router = new RouterVo();
            router.setHidden(menu.getVisible() == 0);
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getIsCache() == 0, menu.getPath()));
            List<SysMenuVo> cMenus = menu.getChildren();
            if (CollUtil.isNotEmpty(cMenus) && MenuConstant.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(getRouters(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getIsCache() == 0, menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(MenuConstant.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    public R<LogonUser> changeJob(IdLongParams params) {
        LogonUser currentUser = LogonUtils.getCurrentUser();
        SysUserJobEntity userJobEntity = this.sysUserJobMapper.selectOne(Wrappers.lambdaQuery(SysUserJobEntity.class)
                .eq(SysUserJobEntity::getUserId, currentUser.getUserId())
                .eq(SysUserJobEntity::getId, params.getId())
                .eq(SysUserJobEntity::getDeleted, 0));
        if (userJobEntity == null) {
            return R.fail("权限不足");
        }
        LogonUser logonUser = new LogonUser();
        logonUser.setUserId(currentUser.getUserId());
        logonUser.setAccount(currentUser.getAccount());
        logonUser.setUsername(currentUser.getUsername());
        logonUser.setJobId(userJobEntity.getId());
        logonUser.setRoleId(userJobEntity.getRoleId());
        List<SysUserJobEntity> userJobList = this.sysUserJobMapper.selectList(Wrappers.lambdaQuery(SysUserJobEntity.class)
                .eq(SysUserJobEntity::getUserId, currentUser.getUserId())
                .eq(SysUserJobEntity::getDeleted, 0));
        logonUser.setJobList(userJobList.stream().map(v -> {
            SysUserJobDTO dto = new SysUserJobDTO();
            dto.setId(v.getId());
            dto.setRoleId(v.getRoleId());
            dto.setRoleName(this.sysRoleMapper.selectById(v.getRoleId()).getRoleName());
            dto.setDeptId(v.getDeptId());
            dto.setDeptAdmin(v.getDeptAdmin());
            return dto;
        }).collect(Collectors.toList()));
        StpUtil.login(logonUser.getJobId());
        String tokenValue = StpUtil.getTokenValue();
        logonUser.setToken(tokenValue);
        StpUtil.getSession().set(BaseConstant.CONTEXT_LOGIN_USERNAME, JsonUtil.toJsonString(logonUser));
        ContextHolder.setCurrentUser(JsonUtil.toJsonString(logonUser));
        return R.success(logonUser);
    }

    public R<Object> logout() {
        LogonUser currentUser = LogonUtils.getCurrentUserQuiet();
        if (currentUser != null) {
            StpUtil.logout(currentUser.getJobId());
        }
        return R.success();
    }


    @Transactional(rollbackFor = Exception.class)
    public R<Object> updatePwd(SysUserUpdatePwdParams params) {
        LogonUser currentUser = LogonUtils.getCurrentUser();
        SysUserEntity sysUserEntity = this.sysUserMapper.selectById(currentUser.getUserId());
        String pwd = DigestUtil.md5Hex(params.getOldPwd() + sysUserEntity.getSalt());
        if (!pwd.equals(sysUserEntity.getPassword())) {
            return R.fail("旧密码不正确");
        }
        sysUserEntity.setPassword(DigestUtil.md5Hex(params.getNewPwd() + sysUserEntity.getSalt()));
        sysUserEntity.setUpdateTime(LocalDateTime.now());
        this.sysUserMapper.updateById(sysUserEntity);
        return R.success();
    }
}
