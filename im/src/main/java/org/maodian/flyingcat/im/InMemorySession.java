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
package org.maodian.flyingcat.im;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.maodian.flyingcat.im.entity.User;

/**
 * @author Cole Wen
 * 
 */
public class InMemorySession implements IMSession {
  public static final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();
  public static final ConcurrentHashMap<User, List<User>> roster = new ConcurrentHashMap<>();
  
  private static final SecurityManager securityManager = new IniSecurityManagerFactory("classpath:shiro.ini").getInstance();
  static {
    SecurityUtils.setSecurityManager(securityManager);
  }
  
  public InMemorySession() {
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.im.Session#register(org.maodian.flyingcat.im.entity
   * .User)
   */
  @Override
  public void register(User user) throws IMException {
    boolean success = users.putIfAbsent(user.getUsername(), user) == null;
    if (!success) {
      throw new IMException(UserError.DUPLICATED_USERNAME);
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.Session#getRoster()
   */
  @Override
  public List<User> getContactList() {
    User user = users.get("");
    return roster.get(user);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#login(java.lang.String, java.lang.String)
   */
  @Override
  public void login(String username, String password) {
    Subject user = SecurityUtils.getSubject();
    if (user.isAuthenticated()) {
      throw new IllegalStateException("The user has already been authenticated");
    }
    UsernamePasswordToken token = new UsernamePasswordToken(username, password);
    try {
      user.login(token);
    } catch (AuthenticationException e) {
      throw new IMException("", e, UserError.AUTHENTICATED_FAILS);
    }
  }

}
