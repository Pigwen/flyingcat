/**
 * 
 */
package org.maodian.flycat.xml;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flycat.bean.xmpp.stream.Stream;
import org.maodian.flycat.xmpp.XmppNamespace;

import com.ctc.wstx.evt.DefaultEventAllocator;

/**
 * @author Cole Wen
 * 
 */
public class XmppXMLEventAllocator implements XMLEventAllocator {

  private static final QName STREAM = new QName(XmppNamespace.STREAM, "stream");
  private final XMLEventAllocator delegate;
  private final XMLEventFactory xef = XMLEventFactory.newInstance();

  /**
   * 
   */
  public XmppXMLEventAllocator() {
    delegate = DefaultEventAllocator.getDefaultInstance();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.util.XMLEventAllocator#newInstance()
   */
  @Override
  public XMLEventAllocator newInstance() {
    return new XmppXMLEventAllocator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.util.XMLEventAllocator#allocate(javax.xml.stream.
   * XMLStreamReader)
   */
  @Override
  public XMLEvent allocate(XMLStreamReader reader) throws XMLStreamException {
    return delegate.allocate(reader);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.xml.stream.util.XMLEventAllocator#allocate(javax.xml.stream.
   * XMLStreamReader, javax.xml.stream.util.XMLEventConsumer)
   */
  @Override
  public void allocate(XMLStreamReader reader, XMLEventConsumer consumer) throws XMLStreamException {
    XMLEvent event = allocate(reader);
    if (event != null) {
      consumer.add(event);
      if (event.isStartElement()) {
        StartElement startElement = event.asStartElement();
        QName name = startElement.getName();
        if (STREAM.equals(name)) {
          EndElement endElement = xef.createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), startElement.getNamespaces());
          
          String from = getAttributeByName(startElement, "from", null);
          String to = getAttributeByName(startElement, "to", null);
          String version = getAttributeByName(startElement, "version", "1.0");
          String lang = getAttributeByName(startElement, new QName(XMLConstants.XML_NS_URI, "lang"), "en");
          
          Stream stream = new Stream.Builder(startElement, endElement)
              .from(from).to(to).version(version).lang(lang).build();
          consumer.add(stream);
        }
      }
    }
  }

  private static String getAttributeByName(StartElement element, String localpart, String defaultValue) {
    String value = element.getAttributeByName(new QName("from")).getValue();
    return StringUtils.defaultString(value, defaultValue);
  }
  
  private static String getAttributeByName(StartElement element, QName qname, String defaultValue) {
    String value = element.getAttributeByName(qname).getValue();
    return StringUtils.defaultString(value, defaultValue);
  }
}
