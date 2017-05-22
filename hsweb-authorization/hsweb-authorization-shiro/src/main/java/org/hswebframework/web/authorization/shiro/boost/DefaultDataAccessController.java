package org.hswebframework.web.authorization.shiro.boost;

import org.hswebframework.web.authorization.access.DataAccessConfig;
import org.hswebframework.web.authorization.access.DataAccessController;
import org.hswebframework.web.authorization.access.DataAccessHandler;
import org.hswebframework.web.authorization.shiro.boost.handler.CustomDataAccessHandler;
import org.hswebframework.web.authorization.shiro.boost.handler.OwnCreatedDataAccessHandler;
import org.hswebframework.web.authorization.shiro.boost.handler.ScriptDataAccessHandler;
import org.hswebframework.web.boost.aop.context.MethodInterceptorParamContext;

import java.util.LinkedList;
import java.util.List;

/**
 * 默认的行级权限控制.通过获取DataAccessHandler进行实际处理
 *
 * @author zhouhao
 * @see DataAccessHandler
 * @since 3.0
 */
public final class DefaultDataAccessController implements DataAccessController {

    private DataAccessController parent;

    private List<DataAccessHandler> handlers = new LinkedList<>();

    public DefaultDataAccessController() {
        this(null);
    }

    public DefaultDataAccessController(DataAccessController parent) {
        if (parent == this) throw new UnsupportedOperationException();
        this.parent = parent;
        addHandler(new CustomDataAccessHandler());
        addHandler(new OwnCreatedDataAccessHandler());
        addHandler(new ScriptDataAccessHandler());
    }

    @Override
    public boolean doAccess(DataAccessConfig access, MethodInterceptorParamContext params) {
        if (parent != null) parent.doAccess(access, params);
        return handlers.parallelStream()
                // TODO: 17-3-28 可以换成access对应的handler以提高效率
                .filter(handler -> handler.isSupport(access))
                .anyMatch(handler -> handler.handle(access, params));
    }

    public DefaultDataAccessController addHandler(DataAccessHandler handler) {
        handlers.add(handler);
        return this;
    }

    public void setHandlers(List<DataAccessHandler> handlers) {
        this.handlers = handlers;
    }

    public List<DataAccessHandler> getHandlers() {
        return handlers;
    }
}
