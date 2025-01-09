package com.cczj.urlservice.service.sys;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cczj.common.base.IdLongParams;
import com.cczj.common.base.R;
import com.cczj.common.bean.params.sys.SysDeptListParams;
import com.cczj.common.bean.params.sys.SysDeptParams;
import com.cczj.common.entity.sys.SysDeptEntity;
import com.cczj.common.enums.CommonStatusEnum;
import com.cczj.urlservice.mapper.sys.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    @Transactional(rollbackFor = Exception.class)
    public R<Object> addDept(SysDeptParams params) {
        SysDeptEntity exist = this.sysDeptMapper.selectOne(Wrappers.lambdaQuery(SysDeptEntity.class)
                .eq(SysDeptEntity::getDeptName, params.getDeptName())
                .eq(SysDeptEntity::getDeleted, 0));
        if (exist != null) {
            return R.fail("部门名称重复");
        }
        SysDeptEntity parent = this.sysDeptMapper.selectOne(Wrappers.lambdaQuery(SysDeptEntity.class)
                .eq(SysDeptEntity::getId, params.getParentId())
                .eq(SysDeptEntity::getDeleted, 0));
        if (parent == null) {
            return R.fail("上级部门不存在");
        }
        if (!CommonStatusEnum.normal.getValue().equals(parent.getStatus())) {
            return R.fail("上级部门处于停用状态，无法在此部门下增加下级");
        }
        SysDeptEntity sysDept = new SysDeptEntity();
        BeanUtils.copyProperties(params, sysDept);
        sysDept.setId(null);
        sysDept.setAncestors(parent.getAncestors() + "," + parent.getId());
        this.sysDeptMapper.insert(sysDept);
        return R.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public R<Object> editDept(SysDeptParams params) {
        if (params.getId() == null) {
            return R.fail("缺少部门id");
        }
        if (params.getId().equals(params.getParentId())) {
            return R.fail("上级部门不能是自己");
        }
        SysDeptEntity sysDept = this.sysDeptMapper.selectById(params.getId());
        if (!params.getDeptName().equals(sysDept.getDeptName())) {
            SysDeptEntity exist = this.sysDeptMapper.selectOne(Wrappers.lambdaQuery(SysDeptEntity.class)
                    .ne(SysDeptEntity::getId, sysDept.getId())
                    .eq(SysDeptEntity::getDeptName, params.getDeptName())
                    .eq(SysDeptEntity::getDeleted, 0));
            if (exist != null) {
                return R.fail("部门名称重复");
            }
        }
        if (!CommonStatusEnum.normal.getValue().equals(params.getStatus())) {
            Integer hasChild = this.sysDeptMapper.selectHasChild(sysDept.getId());
            if (hasChild != null && hasChild > 1) {
                return R.fail("此部门下存在正使用的子部门，无法停用");
            }
        }
        SysDeptEntity parent = this.sysDeptMapper.selectById(params.getParentId());
        BeanUtils.copyProperties(params, sysDept);
        sysDept.setAncestors(parent.getAncestors() + "," + parent.getId());
        this.sysDeptMapper.updateById(sysDept);
        return R.success();
    }

    public R<List<SysDeptEntity>> pageList(SysDeptListParams params) {
        List<SysDeptEntity> deptList = this.sysDeptMapper.selectList(Wrappers.lambdaQuery(SysDeptEntity.class)
                .eq(SysDeptEntity::getDeleted, 0)
                .eq(params.getStatus() != null, SysDeptEntity::getStatus, params.getStatus())
                .like(StrUtil.isNotBlank(params.getDeptName()), SysDeptEntity::getDeptName, params.getDeptName())
                .orderByAsc(SysDeptEntity::getParentId, SysDeptEntity::getOrderNum));
        return R.success(deptList);
    }

    public R<SysDeptEntity> info(IdLongParams params) {
        return R.success(this.sysDeptMapper.selectById(params.getId()));
    }

    public R<Object> delete(IdLongParams params) {
        Integer hasChild = this.sysDeptMapper.selectHasChild(params.getId());
        if (hasChild != null && hasChild > 1) {
            return R.fail("此部门下存在正使用的子部门，无法停用");
        }
        this.sysDeptMapper.update(new SysDeptEntity(), Wrappers.lambdaUpdate(SysDeptEntity.class)
                .set(SysDeptEntity::getDeleted, 1)
                .eq(SysDeptEntity::getId, params.getId()));
        return R.success();
    }

    public R<List<Tree<Long>>> deptTreeList(IdLongParams params) {
        List<SysDeptEntity> deptList = this.sysDeptMapper.selectList(Wrappers.lambdaQuery(SysDeptEntity.class)
                .eq(SysDeptEntity::getDeleted, 0));
        List<TreeNode<Long>> treeNodeList = deptList.stream().map(dept -> {
            TreeNode<Long> treeNode = new TreeNode<>();
            treeNode.setId(dept.getId());
            treeNode.setParentId(dept.getParentId());
            treeNode.setName(dept.getDeptName());
            treeNode.setWeight(dept.getOrderNum());
            return treeNode;
        }).collect(Collectors.toList());
        List<Tree<Long>> treeList = TreeUtil.build(treeNodeList, params.getId() != null ? params.getId() : 0L);
        return R.success(treeList);
    }
}
