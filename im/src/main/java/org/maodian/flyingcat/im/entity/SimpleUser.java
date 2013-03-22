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

import org.springframework.data.mongodb.core.mapping.Field;


/**
 * @author Cole Wen
 * 
 */
public class SimpleUser extends AbstractEntity {
  public static final String USERNAME = "uid";
  public static final String NICK = "nick";
  
  @Field(USERNAME)
  private final String username;
  
  @Field(NICK)
  private final String nickname;

  /**
   * @param username
   * @param nickname
   */
  public SimpleUser(String username, String nickname) {
    this.username = username;
    this.nickname = nickname;
  }

  public String getUsername() {
    return username;
  }

  public String getNickname() {
    return nickname;
  }

  @Override
  public String toString() {
    return "SimpleUser [username=" + username + ", nickname=" + nickname + ", toString()=" + super.toString() + "]";
  }
}
