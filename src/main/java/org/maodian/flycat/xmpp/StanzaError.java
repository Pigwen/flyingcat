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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cole Wen
 *
 */
public class StanzaError implements Stanzas, XmppError {
  private static final Map<Class<? extends Stanzas>, String> TAG_LIST = new HashMap<>();
  {
    TAG_LIST.put(InfoQuery.class, "iq");
  }
  
  private final Stanzas stanzas;
  private final StanzaErrorCondition condition;
  private final Type errorType;
  private final String xml;
  
  private StanzaError(Stanzas stanzas, StanzaErrorCondition condition, Type errorType) {
    this(stanzas, condition, errorType, stanzas.getFrom());
  }
  
  /**
   * @param stanzas
   * @param condition
   * @param errorType
   * @param generator
   */
  private StanzaError(Stanzas stanzas, StanzaErrorCondition condition, Type errorType, String generator) {
    this.stanzas = stanzas;
    this.condition = condition;
    this.errorType = errorType;
    xml = computeXML(condition);
  }
  
  private String computeXML(StanzaErrorCondition condition) {
    String tag = TAG_LIST.get(stanzas.getClass());
    StringBuilder builder = new StringBuilder("<").append(tag).append(" from=\"").append(getFrom())
        .append("\" to=\"").append(getTo()).append("\" type=\"").append(getType()).append("\"><error type=\"")
        .append(errorType).append("\">").append(condition.toXML()).append("</error></").append(tag).append(">");
    return builder.toString();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.XmppError#getNumber()
   */
  @Override
  public int getNumber() {
    return condition.getNumber();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.XmppError#toXML()
   */
  @Override
  public String toXML() {
    return xml;
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getTo()
   */
  @Override
  public String getTo() {
    return stanzas.getFrom();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getId()
   */
  @Override
  public String getId() {
    return stanzas.getId();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getLanguage()
   */
  @Override
  public String getLanguage() {
    return stanzas.getLanguage();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getFrom()
   */
  @Override
  public String getFrom() {
    return stanzas.getTo();
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getType()
   */
  @Override
  public String getType() {
    return ERROR;
  }

  public static enum Type {
    AUTH,
    CANCEL,
    CONTINUE,
    MODIFY,
    WAIT
  }
}
