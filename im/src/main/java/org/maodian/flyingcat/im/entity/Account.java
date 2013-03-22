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

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Cole Wen
 * 
 */
@Document(collection = "account")
public class Account extends SimpleUser {
  private String password;
  private final List<SimpleUser> contacts = new ArrayList<>();
  
  Account() {
    this(null, null, null);
  }

  /**
   * @param username
   * @param password
   */
  public Account(String username, String nickname, String password) {
    super(username, nickname);
    this.password = password;
  }

  public Account(String username, String nickname) {
    this(username, nickname, null);
  }

  public Account(String username) {
    this(username, username);
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
}
