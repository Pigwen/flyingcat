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

import org.apache.commons.lang3.StringUtils;

/**
 * <stream:error>
     <unsupported-encoding xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>
   </stream:error>
 * @author Cole Wen
 *
 */
public enum StreamError implements XmppError {
  BAD_FORMAT(1, "bad-format"),
  BAD_NAMESPACE_PREFIX(2, "bad-namespace-prefix"),
  CONFLICT(3, "conflict"),
  CONNECTION_TIMEOUT(4, "connection-timeout"),
  HOST_GONE(5, "host-gone"),
  HOST_UNKNOWN(6, "host-unknown"),
  IMPROPER_ADDRESSING(7, "improper-addressing"),
  INTERNAL_SERVER_ERROR(8, "internal-server-error"),
  INVALID_FROM(9, "invalid-from"),
  INVALID_NAMESPACE(10, "invalid-namespace"),
  INVALID_XML(11, "invalid-xml"),
  NOT_AUTHORIZED(12, "not-authorized"),
  NOT_WELL_FORMED(13, "not-well-formed"),
  POLICY_VIOLATION(14, "policy-violation"),
  REMOTE_CONNECTION_FAILED(15, "remote-connection-failed"),
  RESET(16, "reset"),
  RESOURCE_CONSTRAINT(17, "resource-constraint"),
  RESTRICTED_XML(18, "restricted-xml"),
  SEE_OTHER_HOST(19, "see-other-host"),
  SYSTEM_SHUTDOWN(20, "system-shutdown"),
  UNDEFINED_CONDITION(21, "undefined-condition"),
  UNSUPPORTED_ENCODING(22, "unsupported-encoding"),
  UNSUPPORTED_FEATURE(23, "unsupported-feature"),
  UNSUPPORTED_STANZA_TYPE(24, "unsupported-stanza-type"),
  UNSUPPORTED_VERSION(25, "unsupported-version");
  
  private static final int FACTOR = 10000;
  private final int number;
  private final String xml;
  
  private StreamError(int number, String condition) {
    this(number, condition, null, null);
  }
  
  private StreamError(int number, String condition, String description, String appElement) {
    this.number = FACTOR + number;
    xml = computeXML(condition, description, appElement);
  }
  
  private String computeXML(String condition, String description, String appElement) {
    StringBuilder builder = new StringBuilder("<stream:error><").append(condition).append(" xmlns=\"")
        .append(XmppNamespace.STREAM).append("\"");
    if (StringUtils.isBlank(description)) {
      builder.append("/>");
    } else {
      builder.append("><text xmlns=\"").append(XmppNamespace.STREAM).append("\" xml:lang=\"en\">")
          .append(description).append("</text></").append(condition).append(">");
    }
    
    if (StringUtils.isNotBlank(appElement)) {
      builder.append(appElement);
    }
    builder.append("</stream:error>");
    return builder.toString();
  }

  @Override
  public int getNumber() {
    return number;
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.XmppError#toXML()
   */
  @Override
  public String toXML() {
    return xml;
  }
}
