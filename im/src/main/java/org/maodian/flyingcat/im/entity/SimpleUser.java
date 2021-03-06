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

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Cole Wen
 * 
 */
@Document
public class SimpleUser {
  public static final String USERNAME = "uid";
  public static final String NICK = "nick";
  public static final String SUB_STATE = "stat";
  public static final String PENDING_OUT = "pout";
  public static final String PENDING_IN = "pin";

  @Field(USERNAME)
  private String username;

  @Field(NICK)
  private String nickname;

  @Field(SUB_STATE)
  private SubState subState;

  @Field(PENDING_OUT)
  private boolean pendingOut;

  @Field(PENDING_IN)
  private boolean pendingIn;

  /**
   * @param username
   * @param nickname
   */
  public SimpleUser(String username, String nickname) {
    this.username = username;
    this.nickname = nickname;
    subState = SubState.NONE;
    pendingOut = false;
    pendingIn = false;
  }

  public String getUsername() {
    return username;
  }

  public String getNickname() {
    return nickname;
  }

  public SubState getSubState() {
    return subState;
  }

  public void setSubState(SubState subState) {
    this.subState = subState;
  }

  public boolean isPendingOut() {
    return pendingOut;
  }

  public void setPendingOut(boolean pendingOut) {
    this.pendingOut = pendingOut;
  }

  public boolean isPendingIn() {
    return pendingIn;
  }

  public void setPendingIn(boolean pendingIn) {
    this.pendingIn = pendingIn;
  }

  @Override
  public String toString() {
    return "SimpleUser [username=" + username + ", nickname=" + nickname + ", subState=" + subState + ", pendingOut="
        + pendingOut + ", pendingIn=" + pendingIn + "]";
  }

  public static enum SubState {
    NONE, FROM, TO, BOTH;

    public static SubState fromString(String str) {
      String type = str.toUpperCase();
      switch (type) {
      case "NONE":
        return NONE;
      case "FROM":
        return FROM;
      case "TO":
        return TO;
      case "BOTH":
        return BOTH;
      default:
        // TODO: STANZAS error
        throw new RuntimeException("SubState not wellformed");
      }
    }
  }
}
