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
package org.maodian.flyingcat.xmpp;

/**
 * @author Cole Wen
 * 
 */
public class Contact {
  public static final String SUB_NONE = "none";
  public static final String SUB_TO = "to";
  public static final String SUB_FROM = "from";
  public static final String SUB_BOTH = "both";
  public static final String SUB_REMOVE = "remove";

  private final String jabberId;
  private final String subscription;
  private String name;
  private boolean approved;
  private String ask;

  /**
   * @param jabberId
   */
  public Contact(String jabberId) {
    this(jabberId, SUB_NONE, null, false, null);
  }

  /**
   * @param jabberId
   * @param name
   * @param approved
   * @param ask
   */
  public Contact(String jabberId, String subscription, String name, boolean approved, String ask) {
    this.jabberId = jabberId;
    this.subscription = subscription;
    this.name = name;
    this.approved = approved;
    this.ask = ask;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isApproved() {
    return approved;
  }

  public void setApproved(boolean approved) {
    this.approved = approved;
  }

  public String getAsk() {
    return ask;
  }

  public void setAsk(String ask) {
    this.ask = ask;
  }

  public String getJabberId() {
    return jabberId;
  }

  public String getSubscription() {
    return subscription;
  }

}
