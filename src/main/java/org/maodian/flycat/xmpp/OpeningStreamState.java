/**
 * 
 */
package org.maodian.flycat.xmpp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.maodian.flycat.holder.XMLInputFactoryHolder;
import org.maodian.flycat.holder.XMLOutputFactoryHolder;


/**
 * @author Cole Wen
 *
 */
public class OpeningStreamState implements State {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.State#handle(org.maodian.flycat.xmpp.XmppContext, java.lang.String)
   */
  @Override
  public String handle(XmppContext context, String xml) {
    try (Reader reader = new StringReader(xml)) {
      try {
        XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
        xmlsr.nextTag();
        QName root = new QName(XmppNamespace.STREAM, "stream");
        if (!root.equals(xmlsr.getName())) {
          //TODO: Deal with invalid stream
          throw new RuntimeException("Deal with invalid stream");
        }
        
        StringWriter writer = new StringWriter(256);
        XMLStreamWriter xmlsw = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(writer);
        xmlsw.writeStartDocument();
        xmlsw.writeStartElement("stream", "stream", XmppNamespace.STREAM);
        xmlsw.writeNamespace("stream", XmppNamespace.STREAM);
        xmlsw.writeDefaultNamespace(XmppNamespace.CONTENT);
        
        xmlsw.writeAttribute("id", RandomStringUtils.randomAlphabetic(32));
        xmlsw.writeAttribute("version", "1.0");
        xmlsw.writeAttribute(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI, "lang", "en");
        String from = xmlsr.getAttributeValue(null, "from");
        if (from != null) {
          xmlsw.writeAttribute("to", from);
        }
        
        // features
        xmlsw.writeStartElement(XmppNamespace.STREAM, "features");
        xmlsw.writeStartElement("starttls");
        xmlsw.writeDefaultNamespace(XmppNamespace.TLS);
        xmlsw.writeEmptyElement("required");
        xmlsw.writeEndElement();
        xmlsw.writeEndElement();
        
        return writer.toString();
        
      } catch (XMLStreamException e) {
        e.printStackTrace();
      }
    } catch (IOException e1) {
      // silent with exception thrown by close method
    }
    return null;
  }
}
