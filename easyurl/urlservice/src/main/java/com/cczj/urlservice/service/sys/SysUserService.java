package com.cczj.urlservice.service.sys;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cczj.common.base.BizException;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysUserListParams;
import com.cczj.common.bean.params.sys.SysUserParams;
import com.cczj.common.bean.params.sys.SysUserStatusParams;
import com.cczj.common.bean.vo.sys.SysUserJobInfoListVo;
import com.cczj.common.bean.vo.sys.SysUserListVo;
import com.cczj.common.entity.sys.SysRoleEntity;
import com.cczj.common.entity.sys.SysUserEntity;
import com.cczj.common.entity.sys.SysUserJobEntity;
import com.cczj.common.enums.sys.SysRoleEnum;
import com.cczj.common.enums.sys.SysUserStatusEnum;
import com.cczj.urlservice.mapper.sys.SysRoleMapper;
import com.cczj.urlservice.mapper.sys.SysUserJobMapper;
import com.cczj.urlservice.mapper.sys.SysUserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserJobMapper sysUserJobMapper;

    @Transactional(rollbackFor = Exception.class)
    public R<Object> addUser(SysUserParams params) {
        BizException.verify(StrUtil.isBlank(params.getPassword()), "无效的登录密码");
        BizException.verify(CollUtil.isEmpty(params.getJobList()), "缺少岗位信息");
        SysUserEntity exist = this.sysUserMapper.selectOne(Wrappers.lambdaQuery(SysUserEntity.class)
                .eq(SysUserEntity::getAccount, params.getAccount())
                .eq(SysUserEntity::getDeleted, 0));
        if (exist != null) {
            return R.fail("登录账户重复，请换一个");
        }
        String salt = RandomUtil.randomString(8);
        SysUserEntity sysUser = new SysUserEntity();
        BeanUtils.copyProperties(params, sysUser);
        sysUser.setSalt(salt);
        sysUser.setPassword(DigestUtil.md5Hex(params.getPassword() + salt));
        sysUser.setStatus(SysUserStatusEnum.normal.getValue());
        this.sysUserMapper.insert(sysUser);
        params.getJobList().forEach(v -> {
            SysUserJobEntity sysUserJob = new SysUserJobEntity();
            sysUserJob.setRoleId(v.getRoleId());
            sysUserJob.setDeptId(v.getDeptId());
            SysRoleEntity sysRoleEntity = this.sysRoleMapper.selectById(v.getRoleId());
            sysUserJob.setDeptAdmin(SysRoleEnum.getByValue(sysRoleEntity.getParentId()).getIsAdmin());
            sysUserJob.setUserId(sysUser.getId());
            this.sysUserJobMapper.insert(sysUserJob);
        });
        return R.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Object> editUser(SysUserParams params) {
        BizException.verify(params.getId() == null, "缺少用户id");
        BizException.verify(CollUtil.isEmpty(params.getJobList()), "缺少岗位信息");
        SysUserEntity exist = this.sysUserMapper.selectOne(Wrappers.lambdaQuery(SysUserEntity.class)
                .eq(SysUserEntity::getDeleted, 0)
                .ne(SysUserEntity::getId, params.getId())
                .eq(SysUserEntity::getAccount, params.getAccount()));
        if (exist != null) {
            return R.fail("登录账户重复，请换一个");
        }
        SysUserEntity sysUser = this.sysUserMapper.selectById(params.getId());
        BizException.verify(sysUser == null, "用户不存在");
        if (StrUtil.isNotBlank(params.getPassword())) {
            sysUser.setPassword(DigestUtil.md5Hex(params.getPassword() + sysUser.getSalt()));
        }
        this.sysUserMapper.update(new SysUserEntity(), Wrappers.lambdaUpdate(SysUserEntity.class)
                .set(StrUtil.isNotBlank(params.getPassword()), SysUserEntity::getPassword, sysUser.getPassword())
                .set(SysUserEntity::getUsername, params.getUsername())
                .set(SysUserEntity::getPhone, params.getPhone())
                .set(SysUserEntity::getAccount, params.getAccount())
                .set(SysUserEntity::getUpdateTime, LocalDateTime.now())
                .eq(SysUserEntity::getId, params.getId()));

        List<SysUserJobEntity> oldJobList = this.sysUserJobMapper.selectList(Wrappers.lambdaQuery(SysUserJobEntity.class)
                .eq(SysUserJobEntity::getUserId, params.getId())
                .eq(SysUserJobEntity::getDeleted, 0));

        Map<Long, SysUserJobEntity> oldJobMap = oldJobList.stream().collect(Collectors.toMap(SysUserJobEntity::getId, v -> v));
        Set<Long> noDeleteSet = new HashSet<>();
        params.getJobList().forEach(v -> {
            if (v.getId() == null) {
                //新增的
                SysUserJobEntity sysUserJob = new SysUserJobEntity();
                sysUserJob.setRoleId(v.getRoleId());
                sysUserJob.setDeptId(v.getDeptId());
                SysRoleEntity sysRoleEntity = this.sysRoleMapper.selectById(v.getRoleId());
                sysUserJob.setDeptAdmin(SysRoleEnum.getByValue(sysRoleEntity.getParentId()).getIsAdmin());
                sysUserJob.setUserId(sysUser.getId());
                this.sysUserJobMapper.insert(sysUserJob);
            } else {
                SysUserJobEntity sysUserJob = oldJobMap.get(v.getId());
                if (sysUserJob != null) {
                    //修改的
                    sysUserJob.setRoleId(v.getRoleId());
                    sysUserJob.setDeptId(v.getDeptId());
                    SysRoleEntity sysRoleEntity = this.sysRoleMapper.selectById(v.getRoleId());
                    sysUserJob.setDeptAdmin(SysRoleEnum.getByValue(sysRoleEntity.getParentId()).getIsAdmin());
                    this.sysUserJobMapper.updateById(sysUserJob);
                    noDeleteSet.add(v.getId());
                } else {
                    log.info("无法匹配的岗位id:{}", v.getId());
                }
            }
        });
        oldJobList.stream().filter(v -> !noDeleteSet.contains(v.getId())).forEach(v -> {
            //删除的
            v.setDeleted(1);
            v.setUpdateTime(LocalDateTime.now());
            this.sysUserJobMapper.updateById(v);
        });
        return R.success();
    }

    public R<PageInfo<SysUserListVo>> userList(SysUserListParams params) {
        if (StrUtil.isNotBlank(params.getBeginTime())) {
            params.setBeginTime(params.getBeginTime() + " 00:00:00");
        }
        if (StrUtil.isNotBlank(params.getEndTime())) {
            params.setEndTime(params.getEndTime() + " 23:59:59");
        }
        PageInfo<SysUserListVo> pageInfo = PageHelper.startPage(params.getPageNumber(), params.getPageSize())
                .doSelectPageInfo(
                        () -> this.sysUserMapper.selectUserList(params)
                );
        if (pageInfo.hasContent()) {
            pageInfo.getList().forEach(v -> {
                boolean login = StpUtil.isLogin(v.getId());
                v.setOnline(login ? "在线" : "离线");
                if (login) {
                    long tokenTimeout = StpUtil.getTokenTimeout(StpUtil.getTokenValueByLoginId(1));
                    if (tokenTimeout == -1) {
                        v.setSessionTimeout("永久");
                    } else if (tokenTimeout == -2) {
                        v.setSessionTimeout("无");
                    } else {
                        v.setSessionTimeout(LocalDateTime.now().plusSeconds(tokenTimeout).format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
                    }
                } else {
                    v.setSessionTimeout("-");
                }
            });
        }
        return R.success(pageInfo);
    }

    public R<SysUserListVo> userDetail(IdLongParams params) {
        SysUserEntity sysUser = this.sysUserMapper.selectById(params.getId());
        List<SysUserJobEntity> jobList = this.sysUserJobMapper.selectList(Wrappers.lambdaQuery(SysUserJobEntity.class)
                .eq(SysUserJobEntity::getUserId, params.getId())
                .eq(SysUserJobEntity::getDeleted, 0));
        SysUserListVo vo = new SysUserListVo();
        BeanUtils.copyProperties(sysUser, vo);
        List<SysUserJobInfoListVo> jobInfoListVoList = jobList.stream().map(v -> {
            SysUserJobInfoListVo jobInfo = new SysUserJobInfoListVo();
            BeanUtils.copyProperties(v, jobInfo);
            return jobInfo;
        }).collect(Collectors.toList());
        vo.setJobList(jobInfoListVoList);
        return R.success(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Object> changeStatus(SysUserStatusParams params) {
        SysUserEntity sysUser = this.sysUserMapper.selectById(params.getId());
        if (sysUser.getStatus().equals(params.getStatus())) {
            return R.fail("状态一致，无法修改");
        }
        sysUser.setStatus(params.getStatus());
        sysUser.setUpdateTime(LocalDateTime.now());
        this.sysUserMapper.updateById(sysUser);
        return R.success();
    }
}
