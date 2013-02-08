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
public class XmppException extends RuntimeException {
  private static final long serialVersionUID = -2933264291244180663L;
  private final Map<String, Object> map = new HashMap<>();
  private final XmppError xmppError;
  
  
  public XmppException(String message, Throwable cause, XmppError xmppError) {
    super(message, cause);
    this.xmppError = xmppError;
  }
  
  public XmppException(String message, XmppError xmppError) {
    super(message);
    this.xmppError = xmppError;
  }
  
  public XmppException(XmppError xmppError) {
    super();
    this.xmppError = xmppError;
  }

  public XmppException(Throwable cause, XmppError xmppError) {
    super(cause);
    this.xmppError = xmppError;
  }

  public XmppException set(String key, Object value) {
    map.put(key, value);
    return this;
  }
}
