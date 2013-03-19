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
package org.maodian.flyingcat.im.entity;

import java.util.Date;

import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Cole Wen
 * 
 */
@Document(collection = "session")
public class Session extends AbstractEntity {
  private String subjectId;
  private Date startTimestamp;
  private Date lastAccessTime;
  private String host;

  public String getSubjectId() {
    return subjectId;
  }

  public Date getStartTimestamp() {
    return startTimestamp;
  }

  public Date getLastAccessTime() {
    return lastAccessTime;
  }

  public String getHost() {
    return host;
  }

  public void touch() throws InvalidSessionException {
    lastAccessTime = new Date();
  }

  public Session id(String id) {
    setId(id);
    return this;
  }

  public Session subjectId(String subjectId) {
    this.subjectId = subjectId;
    return this;
  }

  public Session startTimestamp(Date startTimestamp) {
    this.startTimestamp = startTimestamp;
    return this;
  }

  public Session lastAccessTime(Date lastAccessTime) {
    this.lastAccessTime = lastAccessTime;
    return this;
  }

  public Session host(String host) {
    this.host = host;
    return this;
  }

  @Override
  public String toString() {
    return "Session [subjectId=" + subjectId + ", startTimestamp=" + startTimestamp + ", lastAccessTime="
        + lastAccessTime + ", host=" + host + ", getId()=" + getId() + "]";
  }

  public static Session from(org.apache.shiro.session.Session shiroSession) {
    return new Session().host(shiroSession.getHost()).id((String) shiroSession.getId())
        .lastAccessTime(shiroSession.getLastAccessTime()).startTimestamp(shiroSession.getStartTimestamp())
        .subjectId((String) shiroSession.getAttribute("subjectId"));
  }
  
  public static org.apache.shiro.session.Session to(Session session) {
    SimpleSession ss = new SimpleSession(session.getHost());
    ss.setId(session.getId());
    ss.setLastAccessTime(session.getLastAccessTime());
    ss.setStartTimestamp(session.getStartTimestamp());
    return ss;
  }
}
