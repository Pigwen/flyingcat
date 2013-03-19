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

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Cole Wen
 * 
 */
public class MongoSessionDAO extends AbstractSessionDAO {
  private MongoTemplate template;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.shiro.session.mgt.eis.SessionDAO#update(org.apache.shiro.session
   * .Session)
   */
  @Override
  public void update(Session session) throws UnknownSessionException {

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.shiro.session.mgt.eis.SessionDAO#delete(org.apache.shiro.session
   * .Session)
   */
  @Override
  public void delete(Session session) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.shiro.session.mgt.eis.SessionDAO#getActiveSessions()
   */
  @Override
  public Collection<Session> getActiveSessions() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.shiro.session.mgt.eis.AbstractSessionDAO#doCreate(org.apache
   * .shiro.session.Session)
   */
  @Override
  protected Serializable doCreate(Session session) {
    Serializable sessionId = generateSessionId(session);
    assignSessionId(session, sessionId);
    storeSession(sessionId, session);
    return sessionId;
  }

  /**
   * @param sessionId
   * @param session
   */
  private void storeSession(Serializable sessionId, Session session) {
    if (sessionId == null) {
      throw new NullPointerException("id argument cannot be null.");
    }
    template.insert(session, "session");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.shiro.session.mgt.eis.AbstractSessionDAO#doReadSession(java.
   * io.Serializable)
   */
  @Override
  protected Session doReadSession(Serializable sessionId) {
    Session s = template.findById(sessionId, Session.class, "session");
    return s;
  }

  public void setMongoTemplate(MongoTemplate template) {
    this.template = template;
  }

}
