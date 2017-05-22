/*
 *
 *  * Copyright 2016 http://www.hswebframework.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.hswebframework.web.service;

import org.hswebframework.web.commons.entity.GenericEntity;
import org.hswebframework.web.commons.entity.RecordCreationEntity;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframwork.utils.ClassUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通用实体服务类，提供增删改查的默认实现
 *
 * @author zhouhao
 */
@Transactional(rollbackFor = Throwable.class)
public abstract class GenericEntityService<E extends GenericEntity<PK>, PK>
        extends AbstractService<E, PK>
        implements GenericService<E, PK> {

    @SuppressWarnings("unchecked")
    public GenericEntityService() {
        super();
    }

    /**
     * 获取ID生成器,在insert的时候，如果ID为空,则调用生成器进行生成
     *
     * @return IDGenerator
     * @see IDGenerator
     */
    protected abstract IDGenerator<PK> getIDGenerator();

    @Override
    public abstract CrudDao<E, PK> getDao();

    @Override
    public int deleteByPk(PK pk) {
        return createDelete()
                .where(GenericEntity.id, pk)
                .exec();
    }

    @Override
    public int updateByPk(PK pk, E entity) {
        entity.setId(pk);
        tryValidate(entity);
        return createUpdate(entity)
                //如果是RecordCreationEntity则不修改creator_id和creator_time
                .when(ClassUtils.instanceOf(getEntityType(), RecordCreationEntity.class),
                        update -> update.and().excludes(RecordCreationEntity.creatorId, RecordCreationEntity.createTime))
                .where(GenericEntity.id, pk)
                .exec();
    }

    protected int updateByPk(E entity) {
        return updateByPk(entity.getId(), entity);
    }

    @Override
    public int updateByPk(List<E> data) {
        return data.stream()
                .map(this::updateByPk)
                .reduce(Math::addExact)
                .orElse(0);
    }

    @Override
    public PK saveOrUpdate(E entity) {
        if (null != entity.getId() && null != selectByPk(entity.getId())) {
            updateByPk(entity);
        } else {
            insert(entity);
        }
        return entity.getId();
    }

    @Override
    public PK insert(E entity) {
        if (entity.getId() == null) entity.setId(getIDGenerator().generate());
        tryValidate(entity);
        getDao().insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public E selectByPk(PK pk) {
        if (null == pk) return null;
        return createQuery().where(GenericEntity.id, pk).single();
    }

    @Override
    public List<E> selectByPk(List<PK> id) {
        return createQuery().where().in(GenericEntity.id, id).listNoPaging();
    }
}
