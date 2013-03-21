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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.maodian.flyingcat.im.GlobalContext.Actor;
import org.maodian.flyingcat.im.entity.Account;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Cole Wen
 * 
 */
public class MongoSession implements IMSession {
  private Subject subject;
  private MongoTemplate template;
  private GlobalContext globalContext;

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
   * @see org.maodian.flyingcat.im.IMSession#getContactList()
   */
  @Override
  public List<Account> getContactList() {
    return Collections.emptyList();
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
   * @see
   * org.maodian.flyingcat.im.IMSession#saveContact(org.maodian.flyingcat.im
   * .entity.User)
   */
  @Override
  public void saveContact(Account user) {
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
    action(Verb.CREATE, Type.SESSION, token);
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

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#action(org.maodian.flyingcat.im.Verb, org.maodian.flyingcat.im.Type, java.lang.Object, org.maodian.flyingcat.im.Type, java.lang.Object)
   */
  @Override
  public Object action(Verb verb, Type objectType, final Object objectData, Type targetType, final Object targetData) {
    final Actor actor = globalContext.getTemlateActor(verb, objectType);
    if (subject == null) {
      subject = SecurityUtils.getSubject();
    }
    return subject.execute(new Callable<Object>() {
      /* (non-Javadoc)
       * @see java.util.concurrent.Callable#call()
       */
      @Override
      public Object call() throws Exception {
        return actor.action(objectData, targetData);
      }
    });
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#action(org.maodian.flyingcat.im.Verb, org.maodian.flyingcat.im.Type, java.lang.Object)
   */
  @Override
  public Object action(Verb verb, Type objectType, Object objectData) {
    return action(verb, objectType, objectData, null, null);
  }

  @Inject
  void setGlobalContext(GlobalContext globalContext) {
    this.globalContext = globalContext;
  }
}
