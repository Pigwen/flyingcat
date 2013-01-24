package org.maodian.flycat.holder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * 
 * @author Cole Wen
 *
 */
public final class JAXBContextHolder {
  static final JAXBContext context = createJAXBContext();
  
  private static final JAXBContext createJAXBContext() {
    try {
      return JAXBContext.newInstance("ietf.params.xml.ns.xmpp_sasl"
          + ":ietf.params.xml.ns.xmpp_stanzas"
          + ":ietf.params.xml.ns.xmpp_streams"
          + ":ietf.params.xml.ns.xmpp_tls"
          + ":jabber.client"
          + ":jabber.server"
          + ":org.jabber.etherx.streams");
    } catch (JAXBException e) {
      throw new RuntimeException("Creating JAXBContext has failed", e);
    }
  }
  
  public static final JAXBContext getJAXBContext() {
    return context;
  }
}
