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

/**
 * @author Cole Wen
 * 
 */
public class FullJID {
  private final BareJID bareJID;
  private final String resource;

  /**
   * @param uid
   * @param domain
   * @param resource
   */
  public FullJID(String uid, String domain, String resource) {
    bareJID = new BareJID(uid, domain);
    this.resource = resource;
  }

  public BareJID getBareJID() {
    return bareJID;
  }

  public String getResource() {
    return resource;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bareJID == null) ? 0 : bareJID.hashCode());
    result = prime * result + ((resource == null) ? 0 : resource.hashCode());
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
    FullJID other = (FullJID) obj;
    if (bareJID == null) {
      if (other.bareJID != null)
        return false;
    } else if (!bareJID.equals(other.bareJID))
      return false;
    if (resource == null) {
      if (other.resource != null)
        return false;
    } else if (!resource.equals(other.resource))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "FullJID [bareJID=" + bareJID + ", resource=" + resource + "]";
  }

}
