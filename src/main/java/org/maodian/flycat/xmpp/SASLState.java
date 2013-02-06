/**
 * 
 */
package org.maodian.flycat.xmpp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flycat.holder.XMLInputFactoryHolder;
import org.maodian.flycat.holder.XMLOutputFactoryHolder;

/**
 * @author Cole Wen
 *
 */
public class SASLState implements State {
  private static final String SUCCESS_RESPONSE = "<success xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>";
  private StringBuilder cachedXML = new StringBuilder(128);
  private int readCount = 0;

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.State#handle(org.maodian.flycat.xmpp.XmppContext, java.lang.String)
   */
  @Override
  public String handle(XmppContext context, String xml) {
    // since we use '>' as delimiter in the DelimiterBasedFrameDecoder instance,
    // the whole <auth /> node should be transmited completely at the sencond time.
    if (readCount++ < 1) {
      cachedXML.append(xml);
      return "";
    }
    cachedXML.append(xml);
    try (Reader reader = new StringReader(cachedXML.toString())) {
      try {
        XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
        xmlsr.nextTag();
        QName root = new QName(XmppNamespace.SASL, "auth");
        if (!root.equals(xmlsr.getName())) {
          //TODO: Deal with invalid stream
          throw new RuntimeException("Deal with invalid <auth />");
        }
        
        String value = xmlsr.getElementText();
        return SUCCESS_RESPONSE;
        
      } catch (XMLStreamException e) {
        e.printStackTrace();
      }
    } catch (IOException e1) {
      // silent with exception thrown by close method
    }
    return null;
  }

}
