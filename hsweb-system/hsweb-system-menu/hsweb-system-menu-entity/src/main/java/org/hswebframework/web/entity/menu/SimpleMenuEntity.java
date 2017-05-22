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

package org.hswebframework.web.entity.menu;

import org.hswebframework.web.commons.entity.SimpleTreeSortSupportEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 */
public class SimpleMenuEntity extends SimpleTreeSortSupportEntity<String>
        implements MenuEntity {

    //菜单名称
    private String name;

    //备注
    private String describe;

    //权限ID
    private String permissionId;

    //菜单对应的url
    private String url;

    //图标
    private String icon;

    //是否启用
    private boolean enabled = true;

    //子菜单
    private List<SimpleMenuEntity> children;

    @Override
    @SuppressWarnings("unchecked")
    public List<SimpleMenuEntity> getChildren() {
        if (children == null) return null;
        return new ArrayList<>(children);
    }

    @Override
    public void setChildren(List<MenuEntity> children) {
        // TODO: 17-4-6 有待优化
        this.children = ((List) children);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public SimpleMenuEntity clone() {
        SimpleMenuEntity target = (SimpleMenuEntity) super.clone();
        target.setProperties(cloneProperties());
        if (null != getChildren()) {
            target.setChildren(getChildren().stream()
                    .map(MenuEntity::clone)
                    .collect(Collectors.toList()));
        }
        return target;
    }
}
