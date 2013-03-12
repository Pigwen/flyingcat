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

import org.apache.commons.lang3.StringUtils;
import org.maodian.flyingcat.im.entity.User;

/**
 * @author Cole Wen
 * 
 */
public class InMemorySession implements IMSession {
  private static final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<User, List<User>> roster = new ConcurrentHashMap<>();
  private final String username;

  /**
   * @param username
   */
  public InMemorySession(String username) {
    this.username = username;
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
    if (StringUtils.equals(username, user.getUsername())) {
      boolean success = users.putIfAbsent(user.getUsername(), user) == null;
      if (!success) {
        throw new IMException(UserError.DUPLICATED_USERNAME);
      }
    } else {
      throw new IllegalArgumentException("The username doesnot match");
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.Session#getRoster()
   */
  @Override
  public List<User> getContactList() {
    User user = users.get(username);
    return roster.get(user);
  }

}
