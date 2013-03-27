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
package org.maodian.flyingcat.xmpp.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Cole Wen
 * 
 */
public class BareJID {
  private final String uid;
  private final String domain;

  /**
   * @param uid
   * @param domain
   */
  public BareJID(String uid, String domain) {
    this.uid = uid;
    this.domain = domain;
  }

  public String getUid() {
    return uid;
  }

  public String getDomain() {
    return domain;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((domain == null) ? 0 : domain.hashCode());
    result = prime * result + ((uid == null) ? 0 : uid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BareJID other = (BareJID) obj;
    if (domain == null) {
      if (other.domain != null)
        return false;
    } else if (!domain.equals(other.domain))
      return false;
    if (uid == null) {
      if (other.uid != null)
        return false;
    } else if (!uid.equals(other.uid))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "BareJID [uid=" + uid + ", domain=" + domain + "]";
  }

  public static BareJID fromString(String str) {
    int atIndex = StringUtils.indexOf(str, "@");
    if (atIndex < 1) {
      // TODO: JID mailformed stanzs error
      throw new RuntimeException("JID not well formed");
    }
    String uid = StringUtils.substring(str, 0, atIndex);
    int slashIndex = StringUtils.indexOf(str, "/");
    String domain = StringUtils.substring(str, atIndex, slashIndex);
    return new BareJID(uid, domain);
  }
}
