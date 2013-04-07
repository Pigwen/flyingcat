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
package org.maodian.flyingcat.im.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Cole Wen
 * 
 */
@Document
public class SubscriptionRequest {
  public static final String FROM = "from";
  public static final String TYPE = "type";

  @Field(SubscriptionRequest.FROM)
  private final String from;

  @Field(SubscriptionRequest.TYPE)
  private final RequestType type;

  /**
   * @param from
   * @param type
   */
  public SubscriptionRequest(String from, RequestType type) {
    this.from = from;
    this.type = type;
  }

  public String getFrom() {
    return from;
  }

  public RequestType getType() {
    return type;
  }

  public static enum RequestType {
    SUBSCRIBE, UNSUBSCRIBE, SUBSCRIBED, UNSUBSCRIBED;

    public static RequestType fromString(String str) {
      String type = str.toUpperCase();
      switch (type) {
      case "SUBSCRIBE":
        return SUBSCRIBE;
      case "UNSUBSCRIBE":
        return UNSUBSCRIBE;
      case "SUBSCRIBED":
        return SUBSCRIBED;
      case "UNSUBSCRIBED":
        return UNSUBSCRIBED;
      default:
        // TODO: STANZAS error
        throw new RuntimeException("JID not wellformed");
      }
    }
  }
}
