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
public class JabberID {
  private final String uid;
  private final String domain;
  private final String resource;

  /**
   * @param uid
   * @param domain
   */
  private JabberID(String uid, String domain) {
    this(uid, domain, null);
  }

  private JabberID(String uid, String domain, String resource) {
    this.uid = uid;
    this.domain = domain;
    this.resource = resource;
  }

  public String getUid() {
    return uid;
  }

  public String getDomain() {
    return domain;
  }

  public String getResource() {
    return resource;
  }

  public String toBareJID() {
    StringBuilder sb = new StringBuilder(uid).append("@").append(domain);
    return sb.toString();
  }

  public String toFullJID() {
    StringBuilder sb = new StringBuilder(toBareJID());
    if (StringUtils.isNotBlank(resource)) {
      sb.append("/").append(resource);
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((domain == null) ? 0 : domain.hashCode());
    result = prime * result + ((resource == null) ? 0 : resource.hashCode());
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
    JabberID other = (JabberID) obj;
    if (domain == null) {
      if (other.domain != null)
        return false;
    } else if (!domain.equals(other.domain))
      return false;
    if (resource == null) {
      if (other.resource != null)
        return false;
    } else if (!resource.equals(other.resource))
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
    return "JabberID [uid=" + uid + ", domain=" + domain + ", resource=" + resource + "]";
  }

  public static JabberID fromString(String str) {
    int atIndex = StringUtils.indexOf(str, "@");
    if (atIndex < 1) {
      // TODO: JID mailformed stanzs error
      throw new RuntimeException("JID not well formed");
    }
    String uid = StringUtils.substring(str, 0, atIndex);
    int slashIndex = StringUtils.indexOf(str, "/");
    if (slashIndex == str.length()) {
      // TODO: JID mailformed stanzs error
      throw new RuntimeException("JID not well formed");
    }
    if (slashIndex != -1) {
      String domain = StringUtils.substring(str, atIndex + 1, slashIndex);
      String resource = StringUtils.substring(str, slashIndex);
      return new JabberID(uid, domain, resource);
    } else {
      String domain = StringUtils.substring(str, atIndex + 1);
      return new JabberID(uid, domain);
    }
  }

  public static JabberID createInstance(String uid, String domain, String resource) {
    return new JabberID(uid, domain, resource);
  }
}
