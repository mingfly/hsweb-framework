package org.hswebframework.web.example.simple;

import io.swagger.annotations.ApiOperation;
import org.hswebframework.web.AuthorizeException;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.Permission;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.RequiresDataAccess;
import org.hswebframework.web.authorization.annotation.RequiresFieldAccess;
import org.hswebframework.web.commons.entity.Entity;
import org.hswebframework.web.commons.entity.PagerResult;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.QueryController;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.entity.authorization.SimpleUserEntity;
import org.hswebframework.web.entity.authorization.UserEntity;
import org.hswebframework.web.model.authorization.UserModel;
import org.hswebframework.web.service.QueryByEntityService;
import org.hswebframework.web.service.QueryService;
import org.hswebframework.web.service.authorization.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 */
@RestController
@RequestMapping("/test")
@Authorize(permission = "test")
public class TestController implements QueryController<UserEntity, String, QueryParamEntity> {

    @GetMapping("/test1")
    @Authorize(action = "query")
    public ResponseMessage testSimple(Authentication authentication) {
        return ResponseMessage.ok(authentication).exclude(Authentication.class, "attributes");
    }

    @GetMapping("/test2")
//    @RequiresRoles("admin")
    public ResponseMessage test2(Authentication authentication) {
        return ResponseMessage.ok(authentication);
    }

    @GetMapping("/testQuery")
    @Authorize
    @RequiresDataAccess(permission = "test", action = Permission.ACTION_QUERY)
    @RequiresFieldAccess(permission = "test", action = Permission.ACTION_QUERY)
    @ApiOperation("测试查询")
    public ResponseMessage<QueryParamEntity> testQuery(QueryParamEntity entity) {

        /*
        @RequiresFieldAccess 字段级别权限控制
        entity.getExcludes() 自动填充不能访问的字段
        */

        /*
        @RequiresDataAccess 数据级别权限控制
        entity.terms 被嵌入查询条件
        */
        return ResponseMessage.ok(entity);
    }

    @PutMapping("/testUpdate/{id}")
    @RequiresDataAccess(permission = "test", action = Permission.ACTION_UPDATE)
    @RequiresFieldAccess(permission = "test", action = Permission.ACTION_UPDATE)
    public ResponseMessage<UserModel> testUpdate(@PathVariable String id, @RequestBody UserModel model) {
        return ResponseMessage.ok(model);
    }

    @Override
    public TestService getService() {
        return new TestService();
    }

    public static class TestService implements QueryByEntityService<UserEntity>, QueryService<UserEntity, String> {

        @Override
        public UserEntity selectByPk(String id) {
            SimpleUserEntity userEntity = new SimpleUserEntity();
            // 同一个用户
            userEntity.setCreatorId(Authentication.current().orElseThrow(AuthorizeException::new).getUser().getId());
            return userEntity;
        }

        @Override
        public List<UserEntity> selectByPk(List<String> id) {
            return null;
        }

        @Override
        public List<UserEntity> select() {
            return null;
        }

        @Override
        public int count() {
            return 0;
        }

        @Override
        public PagerResult<UserEntity> selectPager(Entity param) {
            return null;
        }

        @Override
        public List<UserEntity> select(Entity param) {
            return null;
        }

        @Override
        public int count(Entity param) {
            return 0;
        }

        @Override
        public UserEntity selectSingle(Entity param) {
            return null;
        }
    }
}
