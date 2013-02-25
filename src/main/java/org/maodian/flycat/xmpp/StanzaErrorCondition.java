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


/**
 * @author Cole Wen
 *
 */
public enum StanzaErrorCondition implements XmppError {
  BAD_REQUEST(1, "bad-request"),
  CONFLICT(2, "conflict"),
  FEATURE_NOT_IMPLEMENTED(3, "feature-not-implemented"),
  FORBIDDEN(4, "forbidden"),
  GONE(5, "gone"),
  INTERNAL_SERVER_ERROR(6, "internal-server-error"),
  ITEM_NOT_FOUND(7, "item-not-found"),
  JID_MAILFORMED(8, "jid-malformed"),
  NOT_ACCEPTABLE(9, "not-acceptable"),
  NOT_ALLOWED(10, "not-allowed"),
  NOT_AUTHORIZED(11, "not-authorized"),
  POLICY_VIOLATION(12, "policy-violation"),
  RECIPIENT_UNAVAILABLE(13, "recipient-unavailable"),
  REDIRECT(14, "redirect"),
  REGISTRATION_REQUIRED(15, "registration-required"),
  REMOTE_SERVER_NOT_FOUND(16, "remote-server-not-found"),
  REMOTE_SERVER_TIMEOUT(17, "remote-server-timeout"),
  RESOURCE_CONSTRAINT(18, "resource-constraint"),
  SERVICE_UNAVAILABLE(19, "service-unavailable"),
  SUBSCRIPTION_REQUIRED(20, "subscription-required"),
  UNDEFINED_CONDITION(21, "undefined-condition"),
  UNEXPECTED_REQUEST(22, "unexpected-request");

  private static final int FACTOR = 30000;
  private final int number;
  private final String xml;
  
  private StanzaErrorCondition(int number, String condition) {
    this(number, condition, null, null);
  }
  
  private StanzaErrorCondition(int number, String condition, String description, String appElement) {
    this.number = FACTOR + number;
    xml = new StringBuilder("<").append(condition).append(" xmlns=\"").append(XmppNamespace.STANZAS)
        .append("\"/>").toString();
  }
  
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.XmppError#getNumber()
   */
  @Override
  public int getNumber() {
    // TODO Auto-generated method stub
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
