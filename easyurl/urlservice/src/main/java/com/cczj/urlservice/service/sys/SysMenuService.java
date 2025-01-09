package com.cczj.urlservice.service.sys;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysMenuListParams;
import com.cczj.common.bean.params.sys.SysMenuParams;
import com.cczj.common.entity.sys.SysMenuEntity;
import com.cczj.common.entity.sys.SysRoleMenuEntity;
import com.cczj.urlservice.mapper.sys.SysMenuMapper;
import com.cczj.urlservice.mapper.sys.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    public R<Object> addMenu(SysMenuParams params) {
//        SysMenuEntity exist = this.sysMenuMapper.selectOne(Wrappers.lambdaQuery(SysMenuEntity.class)
//                .eq(SysMenuEntity::getMenuName, params.getMenuName())
//                .eq(SysMenuEntity::getDeleted, 0)
//                .last("limit 1"));
//        if (exist != null) {
//            return R.fail("菜单名称重复");
//        }
        if (params.getIsFrame() == 1 && !params.getPath().startsWith("http")) {
            return R.fail("外链格式错误");
        }
        SysMenuEntity sysMenu = new SysMenuEntity();
        BeanUtils.copyProperties(params, sysMenu);
        this.sysMenuMapper.insert(sysMenu);
        return R.success();
    }

    public R<Object> editMenu(SysMenuParams params) {
        if (params.getId() == null) {
            return R.fail("缺少菜单id");
        }
        if (params.getId().equals(params.getParentId())) {
            return R.fail("上级菜单不能是自己");
        }
        SysMenuEntity sysMenu = this.sysMenuMapper.selectById(params.getId());
//        SysMenuEntity exist = this.sysMenuMapper.selectOne(Wrappers.lambdaQuery(SysMenuEntity.class)
//                .ne(SysMenuEntity::getId, sysMenu.getId())
//                .eq(SysMenuEntity::getMenuName, params.getMenuName())
//                .eq(SysMenuEntity::getDeleted, 0)
//                .last("limit 1"));
//        if (exist != null) {
//            return R.fail("菜单名称重复");
//        }
        if (params.getIsFrame() == 1 && !params.getPath().startsWith("http")) {
            return R.fail("外链格式错误");
        }
        BeanUtils.copyProperties(params, sysMenu);
        this.sysMenuMapper.updateById(sysMenu);
        return R.success();
    }

    public R<List<SysMenuEntity>> menuList(SysMenuListParams params) {
        List<SysMenuEntity> menuEntityList = this.sysMenuMapper.selectList(Wrappers.lambdaQuery(SysMenuEntity.class)
                .eq(SysMenuEntity::getDeleted, 0)
                .eq(params.getStatus() != null, SysMenuEntity::getStatus, params.getStatus())
                .like(params.getMenuName() != null, SysMenuEntity::getMenuName, params.getMenuName())
                .orderByAsc(SysMenuEntity::getParentId, SysMenuEntity::getOrderNum));
        return R.success(menuEntityList);
    }

    public R<SysMenuEntity> info(IdLongParams params) {
        SysMenuEntity sysMenu = this.sysMenuMapper.selectById(params.getId());
        return R.success(sysMenu);
    }

    public R<Object> delete(IdLongParams params) {
        SysMenuEntity sysMenu = this.sysMenuMapper.selectById(params.getId());
        if (sysMenu != null) {
            SysMenuEntity child = this.sysMenuMapper.selectOne(Wrappers.lambdaQuery(SysMenuEntity.class)
                    .eq(SysMenuEntity::getParentId, sysMenu.getId())
                    .eq(SysMenuEntity::getDeleted, 0)
                    .last("limit 1"));
            if (child != null) {
                return R.fail("存在子菜单，无法删除");
            }
            this.sysMenuMapper.deleteById(params.getId());
            return R.success();
        }
        return R.success();
    }

    public R<List<Tree<Long>>> treeList(IdLongParams params) {
        List<SysMenuEntity> menuEntityList;
        if (params.getId() != null) {
            List<SysRoleMenuEntity> roleMenuEntityList = this.sysRoleMenuMapper.selectList(Wrappers.lambdaQuery(SysRoleMenuEntity.class)
                    .eq(SysRoleMenuEntity::getRoleId, params.getId())
                    .eq(SysRoleMenuEntity::getDeleted, 0));
            List<Long> menuIdList = roleMenuEntityList.stream().map(SysRoleMenuEntity::getMenuId).collect(Collectors.toList());
            menuEntityList = this.sysMenuMapper.selectList(Wrappers.lambdaQuery(SysMenuEntity.class)
                    .eq(SysMenuEntity::getDeleted, 0)
                    .in(SysMenuEntity::getId, menuIdList));
        } else {
            menuEntityList = this.sysMenuMapper.selectList(Wrappers.lambdaQuery(SysMenuEntity.class)
                    .eq(SysMenuEntity::getDeleted, 0));
        }

        List<TreeNode<Long>> treeNodeList = menuEntityList.stream().map(menu -> {
            TreeNode<Long> treeNode = new TreeNode<>();
            treeNode.setId(menu.getId());
            treeNode.setParentId(menu.getParentId());
            treeNode.setName(menu.getMenuName());
            treeNode.setWeight(menu.getOrderNum());
            return treeNode;
        }).collect(Collectors.toList());
        List<Tree<Long>> treeList = TreeUtil.build(treeNodeList, 0L);
        return R.success(treeList);
    }

    private final SysRoleMenuMapper sysRoleMenuMapper;
}
