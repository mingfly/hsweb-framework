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

package org.hswebframework.web.example.oauth2;

import org.hsweb.ezorm.rdb.executor.AbstractJdbcSqlExecutor;
import org.hsweb.ezorm.rdb.executor.SqlExecutor;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.AuthenticationManager;
import org.hswebframework.web.authorization.oauth2.client.OAuth2RequestService;
import org.hswebframework.web.authorization.oauth2.client.response.OAuth2Response;
import org.hswebframework.web.authorization.shiro.oauth2sso.OAuth2SSOAuthorizingListener;
import org.hswebframework.web.commons.entity.factory.EntityFactory;
import org.hswebframework.web.dao.datasource.DataSourceHolder;
import org.hswebframework.web.dao.datasource.DatabaseType;
import org.hswebframework.web.entity.oauth2.client.OAuth2ServerConfigEntity;
import org.hswebframework.web.service.oauth2.client.OAuth2ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 */
@SpringBootApplication
@Configuration
@EnableCaching
@EnableAspectJAutoProxy
public class OAuth2ClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2ClientApplication.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        // 由于没有使用用户管理,
        // 而且暂时没有实现默认的OAuth2相关的权限获取策略,
        // 所以这里使用通过OAuth2进行获取
        // 实现类似sso的功能,这里实际上应该将权限信息存储起来
        return new AuthenticationManager() {
            @Override
            public Authentication getByUserId(String userId) {
                //获取远程的用户权限信息
                return oAuth2RequestService.create("hsweb-oauth-server")
                        .byClientCredentials()
                        .request("oauth2/user-auth-info/" + userId)
                        .get().onError(OAuth2Response.throwOnError)
                        .as(Authentication.class);
            }

            @Override
            public Authentication sync(Authentication authentication) {
                //暂时不支持
                return authentication;
            }
        };
    }

    @Autowired
    EntityFactory             entityFactory;
    @Autowired
    OAuth2ServerConfigService serverConfigService;
    @Autowired
    OAuth2RequestService      oAuth2RequestService;

    @Override
    public void run(String... strings) throws Exception {
        OAuth2ServerConfigEntity entity = entityFactory.newInstance(OAuth2ServerConfigEntity.class);
        entity.setId("hsweb-oauth-server");
        entity.setName("hsweb OAuth2");
        //可以修改hosts文件改为域名
        entity.setApiBaseUrl("http://localhost:8080/");
        entity.setAuthUrl("oauth2/login.html");
        entity.setAccessTokenUrl("oauth2/token");
        //和服务端创建的一致
        entity.setClientId("hsweb_oauth2_example");
        entity.setClientSecret("hsweb_oauth2_example_secret");
        entity.setRedirectUri("http://localhost:8808/");
        //hsweb
        entity.setProvider("hsweb");
        entity.setEnabled(true);
        //add
        serverConfigService.insert(entity);

        OAuth2SSOAuthorizingListener listener = new OAuth2SSOAuthorizingListener(oAuth2RequestService, entity.getId());

        oAuth2RequestService.registerListener(entity.getId(), listener);
    }


    @Bean
    @ConditionalOnMissingBean(SqlExecutor.class)
    public SqlExecutor sqlExecutor(DataSource dataSource) {
        DataSourceHolder.install(dataSource, DatabaseType.h2);
        return new AbstractJdbcSqlExecutor() {
            @Override
            public Connection getConnection() {
                return DataSourceUtils.getConnection(dataSource);
            }

            @Override
            public void releaseConnection(Connection connection) throws SQLException {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
        };
    }
}
