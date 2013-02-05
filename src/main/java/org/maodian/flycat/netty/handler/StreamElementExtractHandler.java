/**
 * 
 */
package org.maodian.flycat.netty.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jabber.etherx.streams.Stream;
import org.maodian.flycat.holder.JAXBContextHolder;
import org.maodian.flycat.holder.XMLInputFactoryHolder;
import org.maodian.flycat.holder.XMLOutputFactoryHolder;
import org.maodian.flycat.xmpp.XmppNamespace;

/**
 * @author Cole Wen
 * 
 */
public class StreamElementExtractHandler extends ChannelInboundMessageHandlerAdapter<String> {
  private static final String XML_DECLARATION_MARK = "<?xml ";

  private StringBuilder xml;
  private boolean recvXmlDeclFlag = false;
  private boolean recvStrmTagFlag = false;
  private String language;
  private Schema schema;
  
  private Unmarshaller unmarshaller;
  private Marshaller marshaller;
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xml = new StringBuilder(256);
    language = "en";
    //SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    //schema = schemaFactory.newSchema(StreamElementExtractHandler.class.getResource("/xsd/streams.xsd"));
    unmarshaller = JAXBContextHolder.getJAXBContext().createUnmarshaller();
    marshaller = JAXBContextHolder.getJAXBContext().createMarshaller();
    super.channelActive(ctx);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * io.netty.channel.ChannelInboundMessageHandlerAdapter#messageReceived(io
   * .netty.channel.ChannelHandlerContext, java.lang.Object)
   */
  @Override
  protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
    // xml declaration
    if (StringUtils.startsWith(msg, XML_DECLARATION_MARK)) {
      if (!recvXmlDeclFlag) {
        xml.append(msg);
        recvXmlDeclFlag = true;
        return;
      }
      // TODO: deal with duplicated xml declaration
      throw new RuntimeException("deal with duplicated xml declaration");
    }

    // get namespace prefix of <stream /> or <prefix:stream />
    if (!recvStrmTagFlag) {
      try (Reader strReader = new StringReader(xml.append(msg).append("</stream:stream>").toString())) {
        XMLStreamReader stmReader = null;
        XMLStreamWriter stmWriter = null;
        try {
          stmReader = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(strReader);
          stmReader.nextTag();
          JAXBElement<Stream> jaxbElement = unmarshaller.unmarshal(stmReader, Stream.class);
          if (jaxbElement.getName().equals(new QName(XmppNamespace.STREAM, "stream"))) {
            StringWriter strWriter = new StringWriter(256);
            stmWriter = XMLOutputFactoryHolder.getXMLOutputFactory().createXMLStreamWriter(strWriter);
            stmWriter.writeStartDocument();
            stmWriter.writeStartElement("stream", "stream", XmppNamespace.STREAM);
            stmWriter.writeDefaultNamespace(XmppNamespace.CONTENT);
            stmWriter.writeNamespace("stream", XmppNamespace.STREAM);
            
            Stream inStm = jaxbElement.getValue();
            if (StringUtils.isNotBlank(inStm.getFrom())) {
              stmWriter.writeAttribute("to", inStm.getFrom());
            }
            if (inStm.getVersion() != null) {
              stmWriter.writeAttribute("version", "1.0");
            }
            stmWriter.writeAttribute(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI, "lang", language);
            stmWriter.writeAttribute("id", RandomStringUtils.randomAlphabetic(32));
            
            // features
            stmWriter.writeStartElement(XmppNamespace.STREAM, "features");
            stmWriter.writeStartElement("starttls");
            stmWriter.writeDefaultNamespace(XmppNamespace.TLS);
            stmWriter.writeEmptyElement("required");
            stmWriter.writeEndElement();
            stmWriter.writeEndElement();
            
            String xmlString = strWriter.toString();
            ChannelFuture future = ctx.write(xmlString);
            future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            ctx.flush();
            recvStrmTagFlag = true;
          }
        } catch (XMLStreamException xse) {
          // TODO: closing the stream
          xse.printStackTrace();
        } finally {
          if (stmReader != null) {
            stmReader.close();
          }
          if (stmWriter != null) {
            stmWriter.close();
          }
        }
      }
            

      //ctx.write(testXml);
      return;
    }

    // TODO: deal with duplicated stream tag
    throw new RuntimeException("deal with duplicated stream tag");
  }
  
  /*private Features supportedFeatures() {
    Features features = new Features();
    List<Object> featureList = features.getAny();
    Starttls starttls = new Starttls();
    starttls.setRequired("");
    featureList.add(starttls);
    return features;
  }*/
}
