package com.cczj.urlservice.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;


@Slf4j
public class SqlSafeInterceptor extends BlockAttackInnerInterceptor {

    @Override
    protected void processDelete(Delete delete, int index, String sql, Object obj) {
        this.checkWhere(delete.getTable().getName(), delete.getWhere(), "禁止全表删除！！！sql:" + sql);
    }

    @Override
    protected void processUpdate(Update update, int index, String sql, Object obj) {
        this.checkWhere(update.getTable().getName(), update.getWhere(), "禁止全表更新！！！sql:" + sql);
    }

}
