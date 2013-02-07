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
import org.maodian.flycat.holder.XMLInputFactoryHolder;
import org.maodian.flycat.holder.XMLOutputFactoryHolder;


/**
 * @author Cole Wen
 *
 */
public class OpeningStreamState implements State {
  private final FeatureType featureType;
  
  public OpeningStreamState(FeatureType featureType) {
    this.featureType = featureType;
  }

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
        
        // only send xml declaration at the first time
        if (featureType == FeatureType.STARTTLS) {
          xmlsw.writeStartDocument();
        }
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
        State nextState = null;
        switch (featureType) {
        case STARTTLS:
          xmlsw.writeStartElement("starttls");
          xmlsw.writeDefaultNamespace(XmppNamespace.TLS);
          xmlsw.writeEmptyElement("required");
          xmlsw.writeEndElement();
          nextState = new StartTLSState();
          break;
        case SASL:
          xmlsw.writeStartElement("mechanisms");
          xmlsw.writeDefaultNamespace(XmppNamespace.SASL);
          xmlsw.writeStartElement("mechanism");
          xmlsw.writeCharacters("PLAIN");
          xmlsw.writeEndElement();
          xmlsw.writeEndElement();
          nextState = new SASLState();
          break;
        case RESOURCE_BIND:
          xmlsw.writeStartElement("bind");
          xmlsw.writeDefaultNamespace(XmppNamespace.BIND);
          xmlsw.writeEmptyElement("required");
          xmlsw.writeEndElement();
          nextState = new ResourceBindState();
          break;
        default:
          throw new IllegalStateException("The code should not reach here");
        }
        xmlsw.writeEndElement();
        
        context.setState(nextState);
        context.setStreamTag(xml);
        return writer.toString();
        
      } catch (XMLStreamException e) {
        e.printStackTrace();
      }
    } catch (IOException e1) {
      // silent with exception thrown by close method
    }
    return null;
  }
  
  public static enum FeatureType {
    STARTTLS,
    SASL,
    RESOURCE_BIND;
  }
}
