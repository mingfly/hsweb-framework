/*
 * Copyright 2016 http://www.hswebframework.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.hswebframework.web.controller.authorization;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hswebframework.web.AuthorizeException;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.Permission;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.commons.entity.PagerResult;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.CreateController;
import org.hswebframework.web.controller.QueryController;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.entity.authorization.UserEntity;
import org.hswebframework.web.logging.AccessLogger;
import org.hswebframework.web.model.authorization.UserModel;
import org.hswebframework.web.service.authorization.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import static org.hswebframework.web.controller.message.ResponseMessage.ok;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 */
@RestController
@RequestMapping("${hsweb.web.mappings.user:user}")
@Authorize(permission = "user")
@AccessLogger("用户管理")
@Api(tags = "user-manager", description = "用户基本信息管理")
public class UserController implements
        QueryController<UserEntity, String, QueryParamEntity>,
        CreateController<UserEntity, String, UserModel> {

    private UserService userService;

    @Override
    @SuppressWarnings("unchecked")
    public UserService getService() {
        return userService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserEntity modelToEntity(UserModel model, UserEntity entity) {
        entity.setName(model.getName());
        entity.setPassword(model.getPassword());
        entity.setUsername(model.getUsername());
        return entity;
    }

    @Override
    public ResponseMessage<PagerResult<UserEntity>> list(QueryParamEntity param) {
        param.excludes("password", "salt");
        return QueryController.super.list(param)
                .exclude(UserEntity.class, "password", "salt");
    }

    @Override
    public ResponseMessage<UserEntity> getByPrimaryKey(@PathVariable String id) {
        return QueryController.super.getByPrimaryKey(id)
                .exclude(UserEntity.class, "password", "salt");
    }

    @Authorize(action = "update")
    @PutMapping(path = "/{id:.+}")
    @AccessLogger("{update_by_primary_key}")
    @ApiOperation("根据ID修改用户信息")
    public ResponseMessage<Void> updateByPrimaryKey(@PathVariable String id,
                                                    @RequestBody UserModel userModel) {
        getService().update(id, modelToEntity(userModel, getService().createEntity()));
        return ok();
    }

    @Authorize(merge = false)
    @PutMapping(path = "/password")
    @AccessLogger("{update_password_login_user}")
    @ApiOperation("修改当前用户的密码")
    public ResponseMessage<Void> updateLoginUserPassword(@RequestParam String password,
                                                         @RequestParam String oldPassword) {

        Authentication authentication = Authentication.current().orElseThrow(AuthorizeException::new);
        getService().updatePassword(authentication.getUser().getId(), oldPassword, password);
        return ok();
    }

    @Authorize(action = Permission.ACTION_UPDATE)
    @PutMapping(path = "/password/{id:.+}")
    @AccessLogger("{update_password_by_id}")
    @ApiOperation("修改指定用户的密码")
    public ResponseMessage<Void> updateByPasswordPrimaryKey(@PathVariable String id,
                                                            @RequestParam String password,
                                                            @RequestParam String oldPassword) {
        getService().updatePassword(id, oldPassword, password);
        return ok();
    }

    @Authorize(action = "enable")
    @PutMapping(path = "/{id}/enable")
    @AccessLogger("{enable_user}")
    @ApiOperation("启用用户")
    public ResponseMessage<Boolean> enable(@PathVariable String id) {
        return ok(getService().enable(id));
    }

    @Authorize(action = "disable")
    @PutMapping(path = "/{id}/disable")
    @AccessLogger("{disable_user}")
    @ApiOperation("禁用用户")
    public ResponseMessage<Boolean> disable(@PathVariable String id) {
        return ok(getService().disable(id));
    }

}
