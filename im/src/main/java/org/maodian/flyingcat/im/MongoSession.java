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

import org.maodian.flyingcat.im.entity.User;

/**
 * @author Cole Wen
 *
 */
public class MongoSession implements IMSession {

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#register(org.maodian.flyingcat.im.entity.User)
   */
  @Override
  public void register(User user) throws IMException {
    
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#getContactList()
   */
  @Override
  public List<User> getContactList() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#removeContact(org.maodian.flyingcat.im.entity.User)
   */
  @Override
  public void removeContact(User user) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#saveContact(org.maodian.flyingcat.im.entity.User)
   */
  @Override
  public void saveContact(User user) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#login(java.lang.String, java.lang.String)
   */
  @Override
  public void login(String username, String password) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.IMSession#destroy()
   */
  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

}
