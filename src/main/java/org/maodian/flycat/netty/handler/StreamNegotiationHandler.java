/**
 * 
 */
package org.maodian.flycat.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Cole Wen
 * 
 */
public class StreamNegotiationHandler extends ChannelInboundMessageHandlerAdapter<String> {
  private static final String XML_DECLARATION = "<?xml ";
  private static final String STREAM_OPEN_TAG = "<stream:stream ";
  private static final String STREAM_CLOSE_TAG = "</stream:stream>";

  private XMLInputFactory xmlInputFactory;
  private StringBuilder xml;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xmlInputFactory = XMLInputFactory.newInstance();
    xml = new StringBuilder(256);
    super.channelActive(ctx);
  }

  @Override
  protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
    msg = msg.trim();
    if (StringUtils.startsWith(msg, XML_DECLARATION)) {
      xml.append(msg);
    } else if (StringUtils.startsWith(msg, STREAM_OPEN_TAG)) {
      xml.append(msg).append(STREAM_CLOSE_TAG);
      JAXBContext jaxbCtx = JAXBContext.newInstance("org.jabber.etherx.streams");
      Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
      /*Stream stream =  (Stream) unmarshaller.unmarshal(new StringReader(xml.toString()));
      String domain = stream.getTo();
      stream.setFrom(domain);
      stream.setFrom(null);
      stream.setId("1234567");
      
      Features features = new Features();
      stream.setFeatures(features);
      Marshaller marshaller = jaxbCtx.createMarshaller();
      StringWriter writer = new StringWriter();
      marshaller.marshal(stream, writer);
      ctx.write(writer.toString());*/
    } else {
      String tagName = StringUtils.substringBetween(msg, "<", " ");
      xml.append("</").append(tagName).append(">");
    }
  }

}