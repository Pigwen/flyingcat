package org.maodian.flycat.bean.xmpp.stream;

import java.io.Writer;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.maodian.flycat.xml.EventType;

public class Stream implements StartElement {

  private final StartElement startElement;
  private final EndElement endElement;

  private final String from;
  private final String id;
  private final String to;
  private final String version;
  private final String lang;

  private Stream(Builder builder) {
    startElement = builder.startElement;
    endElement = builder.endElement;
    from = builder.from;
    id = builder.id;
    to = builder.to;
    version = builder.version;
    lang = builder.lang;
  }

  @Override
  public int getEventType() {
    return EventType.STREAM;
  }

  @Override
  public Location getLocation() {
    return endElement.getLocation();
  }

  @Override
  public boolean isStartElement() {
    return false;
  }

  @Override
  public boolean isAttribute() {
    return false;
  }

  @Override
  public boolean isNamespace() {
    return false;
  }

  @Override
  public boolean isEndElement() {
    return false;
  }

  @Override
  public boolean isEntityReference() {
    return false;
  }

  @Override
  public boolean isProcessingInstruction() {
    return false;
  }

  @Override
  public boolean isCharacters() {
    return false;
  }

  @Override
  public boolean isStartDocument() {
    return false;
  }

  @Override
  public boolean isEndDocument() {
    return false;
  }

  @Override
  public StartElement asStartElement() {
    throw new ClassCastException();
  }

  @Override
  public EndElement asEndElement() {
    throw new ClassCastException();
  }

  @Override
  public Characters asCharacters() {
    throw new ClassCastException();
  }

  @Override
  public QName getSchemaType() {
    return null;
  }

  @Override
  public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
    startElement.writeAsEncodedUnicode(writer);
    endElement.writeAsEncodedUnicode(writer);
  }
  

  @Override
  public QName getName() {
    return startElement.getName();
  }

  @Override
  public Iterator getAttributes() {
    return startElement.getAttributes();
  }

  @Override
  public Iterator getNamespaces() {
    return startElement.getNamespaces();
  }

  @Override
  public Attribute getAttributeByName(QName name) {
    return startElement.getAttributeByName(name);
  }

  @Override
  public NamespaceContext getNamespaceContext() {
    return startElement.getNamespaceContext();
  }

  @Override
  public String getNamespaceURI(String prefix) {
    return startElement.getNamespaceURI(prefix);
  }

  public String getFrom() {
    return from;
  }

  public String getId() {
    return id;
  }

  public String getTo() {
    return to;
  }

  public String getVersion() {
    return version;
  }

  public String getLang() {
    return lang;
  }

  public static class Builder {
    private final StartElement startElement;
    private final EndElement endElement;

    private String from;
    private String id;
    private String to;
    private String version;
    private String lang;

    public Builder(StartElement startElement, EndElement endElement) {
      this.startElement = startElement;
      this.endElement = endElement;
    }

    public Builder from(String from) {
      this.from = from;
      return this;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder to(String to) {
      this.to = to;
      return this;
    }

    public Builder version(String version) {
      this.version = version;
      return this;
    }

    public Builder lang(String lang) {
      this.lang = lang;
      return this;
    }

    public Stream build() {
      return new Stream(this);
    }
  }
}
