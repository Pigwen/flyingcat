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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.maodian.flyingcat.im.entity.Account;

/**
 * @author Cole Wen
 * 
 */
public class InMemorySession implements IMSession {
  private static final Account cole = new Account("cole", "", "11");
  private static final Account cole2 = new Account("cole2", "", "22");
  private static final Account cole3 = new Account("cole3", "", "33");
  
  public static final ConcurrentMap<String, Account> users = new ConcurrentHashMap<String, Account>() {
    {
      put(cole.getUsername(), cole); 
      put(cole2.getUsername(), cole2);
      put(cole3.getUsername(), cole3);
    }
  };
  public static final ConcurrentHashMap<Account, Map<String, Account>> roster = new ConcurrentHashMap<Account, Map<String, Account>>() {
    {
      put(cole, new ConcurrentHashMap<String, Account>() {
        {
          put(cole2.getUsername(), cole2);
          put(cole3.getUsername(), cole3);
        }
      });
    }
  };
  
  private Subject subject;
  
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
  @RequiresGuest
  public void register(Account user) throws IMException {
    boolean success = users.putIfAbsent(user.getUsername(), user) == null;
    if (!success) {
      throw new IMException(UserError.DUPLICATED_USERNAME);
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.Session#getRoster()
   */
  @Override
  @RequiresAuthentication
  public List<Account> getContactList() {
    Account user = users.get((String) subject.getPrincipal());
    return new ArrayList<>(roster.get(user).values());
  }
 
  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#login(java.lang.String, java.lang.String)
   */
  @Override
  @RequiresGuest
  public void login(String username, String password) {
    Subject user = SecurityUtils.getSubject();
    if ((subject != null && subject.isAuthenticated()) || user.isAuthenticated()) {
      throw new IllegalStateException("The user has already been authenticated");
    }
    UsernamePasswordToken token = new UsernamePasswordToken(username, password);
    try {
      user.login(token);
      subject = user;
      Session shiroSession = subject.getSession();
      shiroSession.getId();
    } catch (AuthenticationException e) {
      throw new IMException("", e, UserError.AUTHENTICATED_FAILS);
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#logout()
   */
  @Override
  public void destroy() {
    if (subject != null) {
      subject.logout();
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#removeContact()
   */
  @Override
  @RequiresAuthentication
  public void removeContact(Account user) {
    Account owner = users.get((String) subject.getPrincipal());
    roster.get(owner).remove(user.getUsername());
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#saveContact(org.maodian.flyingcat.im.entity.User)
   */
  @Override
  @RequiresAuthentication
  public void saveContact(Account user) {
    Account owner = users.get((String) subject.getPrincipal());
    roster.get(owner).put(user.getUsername(), user);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#action(org.maodian.flyingcat.im.Verb, org.maodian.flyingcat.im.Type, java.lang.Object, org.maodian.flyingcat.im.Type, java.lang.Object)
   */
  @Override
  public Object action(Verb verb, Type objectType, Object objectData, Type target, Object targetData) {
    return null;
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#action(org.maodian.flyingcat.im.Verb, org.maodian.flyingcat.im.Type, java.lang.Object)
   */
  @Override
  public Object action(Verb verb, Type objectType, Object objectData) {
    return null;
  }

}
