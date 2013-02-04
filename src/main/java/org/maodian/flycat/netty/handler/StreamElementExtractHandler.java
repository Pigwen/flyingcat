/**
 * 
 */
package org.maodian.flycat.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import javax.xml.XMLConstants;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flycat.holder.JAXBContextHolder;

/**
 * @author Cole Wen
 * 
 */
public class StreamElementExtractHandler extends ChannelInboundMessageHandlerAdapter<String> {
  private static final String XML_DECLARATION_MARK = "<?xml ";
  private static final String testXml = "<stream:stream id=\"mnmaiUHfvPcXrDnNMBhgjNYCoDtoZZJl\" version=\"1.0\" xml:lang=\"en\" xmlns=\"jabber:client\" xmlns:ns4=\"urn:ietf:params:xml:ns:xmpp-stanzas\" xmlns:stream=\"http://etherx.jabber.org/streams\" xmlns:ns3=\"urn:ietf:params:xml:ns:xmpp-sasl\" xmlns:ns5=\"urn:ietf:params:xml:ns:xmpp-streams\">"
    + "<stream:features><starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"><required/></starttls></stream:features>";

  private StringBuilder xml;
  private boolean recvXmlDeclFlag = false;
  private boolean recvStrmTagFlag = false;
  private Unmarshaller unmarshaller;
  private Marshaller marshaller;
  private String language;
  private Schema schema;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xml = new StringBuilder(256);
    marshaller = JAXBContextHolder.getJAXBContext().createMarshaller();
    language = "en";
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    schema = schemaFactory.newSchema(StreamElementExtractHandler.class.getResource("/xsd/streams.xsd"));
    unmarshaller = JAXBContextHolder.getJAXBContext().createUnmarshaller();
    unmarshaller.setSchema(schema);
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
      String prefix = StringUtils.startsWith(msg, "<stream ") ? "</stream>" : StringUtils.substringBetween(msg, "<",
          ":stream ");
      String cloesTag = "</" + prefix + ":stream>";
      xml.append(msg).append(cloesTag);

      /*Stream initStream;
      try {
        initStream = (Stream) unmarshaller.unmarshal(new StringReader(xml.toString()));
      } catch (UnmarshalException ue) {
        // TODO: deal with invalid stream xml and respond <invalid-namespace/>
        // or ,bad-format/>
        // 4.8.1. Stream Namespace
        throw new RuntimeException(ue);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      
      Stream respStream = new Stream();
      StringWriter writer = new StringWriter(256);
      XMLStreamWriter streamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(writer);
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
      streamWriter.writeStartDocument();
      streamWriter.writeStartElement("stream", "stream", XmppNamespace.STREAM);
      streamWriter.setPrefix("stream", XmppNamespace.STREAM);
      streamWriter.writeNamespace("stream", XmppNamespace.STREAM);
      streamWriter.writeDefaultNamespace(XmppNamespace.CONTENT);
      streamWriter.writeAttribute("id", RandomStringUtils.randomAlphabetic(32));
      if (StringUtils.isNotBlank(initStream.getFrom())) {
        //respStream.setTo(initStream.getFrom());
        streamWriter.writeAttribute("to", initStream.getFrom());
      }

      //respStream.setId(RandomStringUtils.randomAlphabetic(32));
      if (StringUtils.isNotBlank(initStream.getLang())) {
        // store it for later use
        language = initStream.getLang();
      }
      streamWriter.writeAttribute(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI, "lang", language);
      //respStream.setLang(language);

      // only present version attribute when initial stream presents it
      if (initStream.getVersion() != null) {
//        respStream.setVersion(new BigDecimal("1.0"));
        streamWriter.writeAttribute("version", "1.0");
      }

      respStream.setFeatures(supportedFeatures());
      recvStrmTagFlag = true;

      
      marshaller.marshal(supportedFeatures(), streamWriter);
      streamWriter.writeEndDocument();
      streamWriter.close();
      String xmlString = writer.toString();
      String closeTag = StringUtils.substringAfterLast(xmlString, ">");
      ctx.write(StringUtils.substringBeforeLast(xmlString, "<"));*/
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
