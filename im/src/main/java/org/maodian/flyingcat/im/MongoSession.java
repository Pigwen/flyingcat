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

import java.lang.invoke.MethodHandles;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.maodian.flyingcat.im.entity.Account;
import org.maodian.flyingcat.im.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Cole Wen
 * 
 */
public class MongoSession implements IMSession {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private Subject subject;
  private MongoTemplate template;

  // repositories
  private AccountRepository accountRepository;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.im.IMSession#register(org.maodian.flyingcat.im.entity
   * .User)
   */
  @Override
  public void register(Account account) throws IMException {
    template.insert(account);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.im.IMSession#removeContact(org.maodian.flyingcat.
   * im.entity.User)
   */
  @Override
  public void removeContact(Account user) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.maodian.flyingcat.im.IMSession#login(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void login(String username, String password) {
    if ((subject != null && subject.isAuthenticated())) {
      throw new IllegalStateException("The user has already been authenticated");
    }
    subject = SecurityUtils.getSubject();
    UsernamePasswordToken token = new UsernamePasswordToken(username, password);
    try {
      SecurityUtils.getSubject().login(token);
    } catch (AuthenticationException e) {
      log.warn("User [{}] login failed", token.getPrincipal());
      throw IMException.wrap(e, UserError.AUTHENTICATED_FAILS);
    };
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.maodian.flyingcat.im.IMSession#destroy()
   */
  @Override
  public void destroy() {
    if (subject != null) {
      subject.logout();
    }
  }

  public void setMongoTemplate(MongoTemplate template) {
    this.template = template;
  }

  @Inject
  void setAccountRepository(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public AccountRepository getAccountRepository() {
    return accountRepository;
  }

}
