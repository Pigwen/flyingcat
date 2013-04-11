/*
 * Copyright 2013 - 2013 Cole Wen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.maodian.flyingcat.im.shiro;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.maodian.flyingcat.im.entity.sql.AccountEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Cole Wen
 *
 */
public class SQLRealm extends AuthorizingRealm {
  @PersistenceContext
  private EntityManager entityManager;

  /* (non-Javadoc)
   * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
   */
  @Override
  @Transactional(readOnly = true)
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    UsernamePasswordToken upToken = (UsernamePasswordToken) token;
    String username = upToken.getUsername();
    if (StringUtils.isBlank(username)) {
      throw new AccountException("Null or blank usernames are not allowed by this realm.");
    }
    String hql = "select a from AccountEntity a where a.uid = :uid";
    AccountEntity user = entityManager.createQuery(hql, AccountEntity.class).setParameter("uid", username).getSingleResult();
    if (user == null) {
      throw new AccountException("No user found for username:" + username);
    }
    
    SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(username, user.getPassword(), getName());
    return info;
  }

}
