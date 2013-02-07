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
