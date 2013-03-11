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
 * @author Cole Wen
 *
 */
public class InfoQuery implements Stanzas {
  public static final String GET = "get";
  public static final String SET = "set";
  public static final String RESULT = "result";
  
  private final String id;
  private final String type;
  private String from;
  private String to;
  private String language;
  private Object payload;
  
  private InfoQuery(Builder builder) {
    id = builder.id;
    type = builder.type;
    from = builder.from;
    to = builder.to;
    language = builder.language;
    payload = builder.payload;
  }
  
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getTo()
   */
  @Override
  public String getTo() {
    return to;
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getId()
   */
  @Override
  public String getId() {
    return id;
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getLanguage()
   */
  @Override
  public String getLanguage() {
    return language;
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getFrom()
   */
  @Override
  public String getFrom() {
    return from;
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.Stanzas#getType()
   */
  @Override
  public String getType() {
    return type;
  }
  
  public Object getPayload() {
    return payload;
  }

  public static class Builder implements Stanzas.Builder<InfoQuery> {
    private final String id;
    private final String type;
    private String from;
    private String to;
    private String language;
    private Object payload;
    
    public Builder(String id, String type) {
      this.id = id;
      checkType(type);
      this.type = type;
    }
    
    private void checkType(String type) {
      if (!StringUtils.equals(type, GET) && !StringUtils.equals(type, SET)
          && !StringUtils.equals(type, RESULT) && !StringUtils.equals(type, ERROR)) {
        throw new XmppException("Invalid InfoQuery type", StanzaErrorCondition.BAD_REQUEST)
            .set("type", type);
      }
    }

    public Builder from(String from) {
      this.from = from;
      return this;
    }
    
    public Builder to(String to) {
      this.to = to;
      return this;
    }
    
    public Builder language(String language) {
      this.language = language;
      return this;
    }
    
    public Builder payload(Object payload) {
      this.payload = payload;
      return this;
    }

    /* (non-Javadoc)
     * @see org.maodian.flycat.xmpp.Stanzas.Builder#build()
     */
    @Override
    public InfoQuery build() {
      return new InfoQuery(this);
    }
  }
}
