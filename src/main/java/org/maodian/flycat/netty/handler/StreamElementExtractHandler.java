/**
 * 
 */
package org.maodian.flycat.netty.handler;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jabber.etherx.streams.Features;
import org.jabber.etherx.streams.Stream;
import org.maodian.flycat.holder.JAXBContextHolder;

/**
 * @author Cole Wen
 * 
 */
public class StreamElementExtractHandler extends ChannelInboundMessageHandlerAdapter<String> {
  private static final String XML_DECLARATION_MARK = "<?xml ";

  private StringBuilder xml;
  private boolean recvXmlDeclFlag = false;
  private boolean recvStrmTagFlag = false;
  private Unmarshaller unmarshaller;
  private Marshaller marshaller;
  private String language;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xml = new StringBuilder(256);
    unmarshaller = JAXBContextHolder.getJAXBContext().createUnmarshaller();
    marshaller = JAXBContextHolder.getJAXBContext().createMarshaller();
    language = "en";
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
      String prefix = StringUtils.startsWith(msg, "<stream ") ? "</stream>" 
          : StringUtils.substringBetween(msg, "<", ":stream ");
      String cloesTag = "</" + prefix + ":stream>";
      xml.append(msg).append(cloesTag);
      
      Stream initStream = (Stream) unmarshaller.unmarshal(new StringReader(xml.toString()));
      Stream respStream = new Stream();
      if (StringUtils.isNotBlank(initStream.getFrom())) {
        respStream.setTo(initStream.getFrom());
      }
      
      respStream.setId(RandomStringUtils.randomAlphabetic(32));
      if (StringUtils.isNotBlank(initStream.getLang())) {
        // store it for later use
        language = initStream.getLang();
      } 
      respStream.setLang(language);
      
      // only present version attribute when initial stream presents it
      if (initStream.getVersion() != null) {
        respStream.setVersion(new BigDecimal("1.0"));
      }
      
      respStream.setFeatures(new Features());
      recvStrmTagFlag = true;
      
      StringWriter writer = new StringWriter(256);
      marshaller.marshal(respStream, writer);
      ctx.write(writer.toString());
      return;
    }
    
    // TODO: deal with duplicated stream tag
    throw new RuntimeException("deal with duplicated stream tag");
  }

}
