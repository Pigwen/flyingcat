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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Cole Wen
 * 
 */
@Entity
@Table(name = "fy_contact")
public class ContactEntity extends AbstractEntity {
  private String uid;
  private String nick;
  private String comment;
  private boolean pendingOut;
  private boolean pendingIn;
  private boolean from;
  private boolean to;
  private AccountEntity owner;

  /**
   * 
   */
  public ContactEntity() {
  }

  /**
   * @param uid
   * @param nick
   */
  public ContactEntity(String uid, String nick) {
    this.uid = uid;
    this.nick = nick;
  }

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

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public boolean isOutbound() {
    return pendingOut;
  }

  public void setOutbound(boolean outbound) {
    this.pendingOut = outbound;
  }

  public boolean isInbound() {
    return pendingIn;
  }

  public void setInbound(boolean inbound) {
    this.pendingIn = inbound;
  }
  
  @Transient
  public Pending getPending() {
    if (isInbound() && isOutbound()) {
      return Pending.IN_OUT;
    } else if (isInbound()) {
      return Pending.IN;
    } else if (isOutbound()) {
      return Pending.OUT;
    } else {
      return Pending.NONE;
    }
  }
  
  public void setPending(Pending p) {
    switch (p) {
    case IN:
      setInbound(true);
      setOutbound(false);
      break;
    case OUT:
      setInbound(false);
      setOutbound(true);
      break;
    case IN_OUT:
      setInbound(true);
      setOutbound(true);
      break;
    case NONE:
      setSubFrom(false);
      setSubTo(false);
    default:
      throw new IllegalArgumentException("unrecogonized Pending");
    }
  }

  public boolean isSubFrom() {
    return from;
  }

  public void setSubFrom(boolean from) {
    this.from = from;
  }

  public boolean isSubTo() {
    return to;
  }

  public void setSubTo(boolean to) {
    this.to = to;
  }

  @ManyToOne(optional = false)
  @JoinColumn(name = "owner_id")
  public AccountEntity getOwner() {
    return owner;
  }

  public void setOwner(AccountEntity owner) {
    this.owner = owner;
  }

  @Transient
  public Relationship getRelationship() {
    if (isSubFrom() && isSubTo()) {
      return Relationship.BOTH;
    } else if (isSubFrom()) {
      return Relationship.FROM;
    } else if (isSubTo()) {
      return Relationship.TO;
    } else {
      return Relationship.NONE;
    }
  }

  public void setRelationship(Relationship relationship) {
    switch (relationship) {
    case BOTH:
      setSubFrom(true);
      setSubTo(true);
      break;
    case FROM:
      setSubFrom(true);
      setSubTo(false);
      break;
    case TO:
      setSubTo(true);
      setSubFrom(false);
      break;
    case NONE:
      setSubFrom(false);
      setSubTo(false);
    default:
      throw new IllegalArgumentException("unrecogonized relationship");
    }
  }

  public static enum Relationship {
    NONE, FROM, TO, BOTH;

    public static Relationship fromString(String str) {
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
        throw new RuntimeException("Relationship is not wellformed");
      }
    }
  }

  public static enum Pending {
    NONE, IN, OUT, IN_OUT;

    public static Pending fromString(String str) {
      String type = str.toUpperCase();
      switch (type) {
      case "NONE":
        return NONE;
      case "IN":
        return IN;
      case "OUT":
        return OUT;
      case "IN_OUT":
        return IN_OUT;
      default:
        // TODO: STANZAS error
        throw new RuntimeException("Relationship is not wellformed");
      }
    }
  }
}
