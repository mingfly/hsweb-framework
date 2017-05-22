/*
 *  Copyright 2016 http://www.hswebframework.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.hswebframework.web.entity.authorization;

import org.hswebframework.web.commons.entity.GenericEntity;

import java.util.List;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 */
public interface PermissionEntity extends GenericEntity<String> {

    String getId();

    String getName();

    String getDescribe();

    Byte getStatus();

    void setName(String name);

    void setDescribe(String comment);

    void setStatus(Byte status);

    List<ActionEntity> getActions();

    void setActions(List<ActionEntity> actions);

    List<DataAccessEntity> getDataAccess();

    List<FieldAccessEntity> getFieldAccess();

    void setDataAccess(List<DataAccessEntity> dataAccess);

    void setFieldAccess(List<FieldAccessEntity> fieldAccess);
}
