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

import java.util.Arrays;
import java.util.List;

import org.maodian.flyingcat.xmpp.StanzaError.Type;


/**
 * @author Cole Wen
 *
 */
public enum StanzaErrorCondition implements XmppError {
  BAD_REQUEST(1, "bad-request", Type.MODIFY),
  CONFLICT(2, "conflict", Type.CANCEL),
  FEATURE_NOT_IMPLEMENTED(3, "feature-not-implemented", Type.CANCEL, Type.MODIFY),
  FORBIDDEN(4, "forbidden", Type.AUTH),
  GONE(5, "gone", Type.CANCEL),
  INTERNAL_SERVER_ERROR(6, "internal-server-error", Type.CANCEL),
  ITEM_NOT_FOUND(7, "item-not-found", Type.CANCEL),
  JID_MAILFORMED(8, "jid-malformed", Type.MODIFY),
  NOT_ACCEPTABLE(9, "not-acceptable", Type.MODIFY),
  NOT_ALLOWED(10, "not-allowed", Type.CANCEL),
  NOT_AUTHORIZED(11, "not-authorized", Type.AUTH),
  POLICY_VIOLATION(12, "policy-violation", Type.MODIFY, Type.WAIT),
  RECIPIENT_UNAVAILABLE(13, "recipient-unavailable", Type.WAIT),
  REDIRECT(14, "redirect", Type.MODIFY),
  REGISTRATION_REQUIRED(15, "registration-required", Type.AUTH),
  REMOTE_SERVER_NOT_FOUND(16, "remote-server-not-found", Type.CANCEL),
  REMOTE_SERVER_TIMEOUT(17, "remote-server-timeout", Type.WAIT),
  RESOURCE_CONSTRAINT(18, "resource-constraint", Type.WAIT),
  SERVICE_UNAVAILABLE(19, "service-unavailable", Type.CANCEL),
  SUBSCRIPTION_REQUIRED(20, "subscription-required", Type.AUTH),
  UNDEFINED_CONDITION(21, "undefined-condition"),
  UNEXPECTED_REQUEST(22, "unexpected-request", Type.WAIT, Type.MODIFY);

  private static final int FACTOR = 30000;
  private final int number;
  private final String xml;
  private final List<Type> supportType;
  
  private StanzaErrorCondition(int number, String condition, Type... types) {
    this.number = FACTOR + number;
    xml = new StringBuilder("<").append(condition).append(" xmlns=\"").append(XmppNamespace.STANZAS)
        .append("\"/>").toString();
    supportType = Arrays.asList(types);
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
  
  public boolean accept(Type type) {
    return this == UNDEFINED_CONDITION ? true : supportType.contains(type);
  }
}
