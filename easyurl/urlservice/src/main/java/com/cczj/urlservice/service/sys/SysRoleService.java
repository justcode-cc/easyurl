package com.cczj.urlservice.service.sys;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysRoleParams;
import com.cczj.common.bean.params.sys.SysRoleQueryParams;
import com.cczj.common.dto.KvResult;
import com.cczj.common.entity.sys.SysRoleEntity;
import com.cczj.common.entity.sys.SysRoleMenuEntity;
import com.cczj.common.enums.sys.SysRoleEnum;
import com.cczj.urlservice.mapper.sys.SysRoleMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuService sysRoleMenuService;


    @Transactional(rollbackFor = Exception.class)
    public R<Object> addRole(SysRoleParams params) {
        SysRoleEntity exist = this.sysRoleMapper.selectOne(Wrappers.lambdaQuery(SysRoleEntity.class)
                .eq(SysRoleEntity::getDeleted, 0)
                .eq(SysRoleEntity::getMainRole, 0)
                .eq(SysRoleEntity::getRoleName, params.getRoleName())
                .last("limit 1"));
        if (exist != null) {
            return R.fail("角色名称重复");
        }
        SysRoleEntity sysRoleEntity = new SysRoleEntity();
        BeanUtils.copyProperties(params, sysRoleEntity);
        this.sysRoleMapper.insert(sysRoleEntity);
        List<SysRoleMenuEntity> roleMenuList = params.getMenuIdList().stream().map(menuId -> {
            SysRoleMenuEntity sysRoleMenu = new SysRoleMenuEntity();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(sysRoleEntity.getId());
            return sysRoleMenu;
        }).collect(Collectors.toList());
        this.sysRoleMenuService.saveBatch(roleMenuList);
        return R.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Object> editRole(SysRoleParams params) {
        if (params.getId() == null) {
            return R.fail("缺少id");
        }
        SysRoleEntity sysRole = this.sysRoleMapper.selectById(params.getId());
        if (!params.getRoleName().equals(sysRole.getRoleName())) {
            SysRoleEntity repeat = this.sysRoleMapper.selectOne(Wrappers.lambdaQuery(SysRoleEntity.class)
                    .ne(SysRoleEntity::getId, sysRole.getId())
                    .eq(SysRoleEntity::getMainRole, 0)
                    .eq(SysRoleEntity::getRoleName, params.getRoleName())
                    .eq(SysRoleEntity::getDeleted, 0));
            if (repeat != null) {
                return R.fail("角色名称重复");
            }
        }
        this.sysRoleMenuService.update(new SysRoleMenuEntity(), Wrappers.lambdaUpdate(SysRoleMenuEntity.class)
                .set(SysRoleMenuEntity::getDeleted, 1)
                .set(SysRoleMenuEntity::getUpdateTime, LocalDateTime.now())
                .eq(SysRoleMenuEntity::getRoleId, sysRole.getId())
                .eq(SysRoleMenuEntity::getDeleted, 0));
        List<SysRoleMenuEntity> roleMenuList = params.getMenuIdList().stream().map(menuId -> {
            SysRoleMenuEntity sysRoleMenu = new SysRoleMenuEntity();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(sysRole.getId());
            return sysRoleMenu;
        }).collect(Collectors.toList());
        this.sysRoleMenuService.saveBatch(roleMenuList);
        sysRole.setRoleName(params.getRoleName());
        sysRole.setParentId(params.getParentId());
        sysRole.setUpdateTime(LocalDateTime.now());
        this.sysRoleMapper.updateById(sysRole);
        return R.success();
    }

    public R<List<KvResult>> roleParentList() {
        List<SysRoleEntity> sysRoleEntityList = this.sysRoleMapper.selectList(Wrappers.lambdaQuery(SysRoleEntity.class)
                .eq(SysRoleEntity::getMainRole, 1)
                .eq(SysRoleEntity::getDeleted, 0)
                .orderByAsc(SysRoleEntity::getId));
        List<KvResult> collect = sysRoleEntityList.stream().map(
                v -> new KvResult(v.getId() + "", v.getRoleName())
        ).collect(Collectors.toList());
        return R.success(collect);
    }


    public R<PageInfo<SysRoleEntity>> roleList(SysRoleQueryParams params) {
        PageInfo<SysRoleEntity> pageInfo = PageHelper.startPage(params.getPageNumber(), params.getPageSize()).doSelectPageInfo(
                () -> this.sysRoleMapper.selectList(Wrappers.lambdaQuery(SysRoleEntity.class)
                        .eq(SysRoleEntity::getDeleted, 0)
                        .eq(SysRoleEntity::getMainRole, 0)
                        .eq(StrUtil.isNotBlank(params.getRoleName()), SysRoleEntity::getRoleName, params.getRoleName())
                        .orderByDesc(SysRoleEntity::getId))
        );
        return R.success(pageInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Object> delete(IdLongParams params) {
        SysRoleEntity sysRoleEntity = this.sysRoleMapper.selectById(params.getId());
        sysRoleEntity.setDeleted(1);
        sysRoleEntity.setUpdateTime(LocalDateTime.now());
        this.sysRoleMapper.updateById(sysRoleEntity);
        this.sysRoleMenuService.update(new SysRoleMenuEntity(), Wrappers.lambdaUpdate(SysRoleMenuEntity.class)
                .set(SysRoleMenuEntity::getDeleted, 1)
                .set(SysRoleMenuEntity::getUpdateTime, LocalDateTime.now())
                .eq(SysRoleMenuEntity::getRoleId, sysRoleEntity.getId())
                .eq(SysRoleMenuEntity::getDeleted, 0));
        return R.success();
    }

    public R<SysRoleEntity> info(IdLongParams params) {
        SysRoleEntity sysRoleEntity = this.sysRoleMapper.selectById(params.getId());
        return R.success(sysRoleEntity);
    }

    public R<List<Long>> getRoleMenuIdList(IdLongParams params) {
        List<SysRoleMenuEntity> list = this.sysRoleMenuService.list(Wrappers.lambdaQuery(SysRoleMenuEntity.class)
                .eq(SysRoleMenuEntity::getRoleId, params.getId())
                .eq(SysRoleMenuEntity::getDeleted, 0));
        return R.success(list.stream().map(SysRoleMenuEntity::getMenuId).collect(Collectors.toList()));
    }

    public R<List<KvResult>> roleAllList() {
        List<SysRoleEntity> sysRoleEntities = this.sysRoleMapper.selectList(Wrappers.lambdaQuery(SysRoleEntity.class)
                .eq(SysRoleEntity::getDeleted, 0)
                .ne(SysRoleEntity::getId, 1));
        return R.success(sysRoleEntities.stream().map(v -> new KvResult(v.getId() + "", v.getRoleName())).collect(Collectors.toList()));
    }
}
