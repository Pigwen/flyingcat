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
package org.maodian.flycat.xmpp;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre><failure xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>
 *   <defined-condition/>
 *   [<text xml:lang='langcode'>
 *     OPTIONAL descriptive text
 *   </text>]
 * </failure></pre>
 * @author Cole Wen
 *
 */
public enum SASLError implements XmppError {
  ABORTED(1, "aborted"),
  ACCOUNT_DISABLED(2, "account-disabled"),
  CREDENTIALS_EXPIRED(3, "credentials-expired"),
  ENCRYPTION_REQUIRED(4, "encryption-required"),
  INCORRECT_ENCODING(5, "incorrect-encoding"),
  INVALID_AUTHZID(6, "invalid-authzid"),
  INVALID_MECHANISM(7, "invalid-mechanism"),
  MALFORMED_REQUEST(8, "malformed-request"),
  MECHANISM_TOO_WEAK(9, "mechanism-too-weak"),
  NOT_AUTHORIZED(10, "not-authorized"),
  TEMPORARY_AUTH_FAILURE(11, "temporary-auth-failure");

  private static final int FACTOR = 20000;
  private final int number;
  private final String xml;
  
  private SASLError(int number, String condition) {
    this(number, condition, null, null);
  }
  
  private SASLError(int number, String condition, String description, String appElement) {
    this.number = FACTOR + number;
    xml = computeXML(condition, description, appElement);
  }
  
  private String computeXML(String condition, String description, String appElement) {
    StringBuilder builder = new StringBuilder("<failure xmlns=\"").append(XmppNamespace.SASL).append("\"><")
        .append(condition).append("/>");
    if (StringUtils.isNotBlank(description)) {
      builder.append("<text xml:lang=\"en\">")
        .append(description).append("</text>");
    } 
    builder.append("</failure>");
    return builder.toString();
  }
  
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.XmppError#getNumber()
   */
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
