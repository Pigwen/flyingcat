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
package org.maodian.flyingcat.im.entity.sql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Cole Wen
 * 
 */
@Entity
@Table(name = "fy_account")
public class AccountEntity extends AbstractEntity {
  private String uid;
  private String nick;
  private String password;
  private Map<String, ContactEntity> contacts = new HashMap<>();

  /**
   * 
   */
  public AccountEntity() {
  }

  /**
   * @param username
   */
  public AccountEntity(String uid) {
    this.uid = uid;
    this.nick = uid;
  }

  @Column(unique = true, nullable = false)
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getNick() {
    return nick;
  }

  public void setNick(String nick) {
    this.nick = nick;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Transient
  public Collection<ContactEntity> getContacts() {
    return getContactsMap().values();
  }

  @Transient
  public ContactEntity getContact(String uid) {
    return getContactsMap().get(uid);
  }

  @OneToMany(mappedBy = "owner")
  @MapKey(name = "uid")
  public Map<String, ContactEntity> getContactsMap() {
    return contacts;
  }

  void setContactsMap(Map<String, ContactEntity> contacts) {
    this.contacts = contacts;
  }

}
