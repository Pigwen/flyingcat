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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Cole Wen
 * 
 */
@Document(collection = Account.COLLECTION_NAME)
public class Account extends AbstractEntity {
  public static final String COLLECTION_NAME = "account";
  public static final String USERNAME = "uid";
  public static final String NICK = "nick";
  public static final String PASSWORD = "pwd";
  public static final String CONTACTS = "cont";
  public static final String UNREAD_REQUEST = "urq";

  @Field(USERNAME)
  @Indexed(unique = true)
  private final String username;

  @Field(NICK)
  private final String nickname;

  @Field(PASSWORD)
  private String password;

  @Field(CONTACTS)
  private final List<SimpleUser> contacts = new ArrayList<>();

  @Field(UNREAD_REQUEST)
  private final List<SubscriptionRequest> unreadSubscriptionRequests = new ArrayList<>();

  Account() {
    this(null, null, null);
  }

  /**
   * @param username
   * @param password
   */
  public Account(String username, String nickname, String password) {
    this.username = username;
    this.nickname = nickname;
    this.password = password;
  }

  public Account(String username, String nickname) {
    this(username, nickname, null);
  }

  public Account(String username) {
    this(username, username);
  }

  public String getUsername() {
    return username;
  }

  public String getNickname() {
    return nickname;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void addContact(SimpleUser... contactList) {
    for (SimpleUser c : contactList) {
      contacts.add(c);
    }
  }

  public void addContact(Collection<SimpleUser> contactList) {
    contacts.addAll(contactList);
  }

  public List<SimpleUser> getContactList() {
    return Collections.unmodifiableList(contacts);
  }
  
  public void addUnreadSubscriptionRequest(SubscriptionRequest... reqs) {
    for (SubscriptionRequest req : reqs) {
      unreadSubscriptionRequests.add(req);
    }
  }
  
  public void addUnreadSubscriptionRequest(Collection<SubscriptionRequest> reqs) {
    unreadSubscriptionRequests.addAll(reqs);
  }

  public List<SubscriptionRequest> getUnreadSubscriptionRequests() {
    return Collections.unmodifiableList(unreadSubscriptionRequests);
  }

  @Override
  public String toString() {
    return "Account [username=" + username + ", nickname=" + nickname + ", password=" + password + ", contacts="
        + contacts + ", unreadSubscriptionRequests=" + unreadSubscriptionRequests + "]";
  }

}
