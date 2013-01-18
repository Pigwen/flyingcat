/**
 * 
 */
package org.maodian.flycat.netty.handler;

import javax.xml.stream.XMLInputFactory;

import org.apache.commons.lang3.StringUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * @author Cole Wen
 *
 */
public class XmlToObjectHandler extends MessageToMessageDecoder<String> {
  private static final String XML_DECLARATION = "<?xml version='1.0' ";
  private static final String STREAM_START_TAG = "<stream:stream ";
  private static final String STREAM_CLOSE_TAG = "</stream:stream>";

  private XMLInputFactory xmlInputFactory;
  private StringBuilder xml;
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xmlInputFactory = XMLInputFactory.newInstance();
    xml = new StringBuilder(256);
    super.channelActive(ctx);
  }
  /* (non-Javadoc)
   * @see io.netty.handler.codec.MessageToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, java.lang.Object)
   */
  @Override
  protected Object decode(ChannelHandlerContext ctx, String msg) throws Exception {
    if (StringUtils.startsWith(msg, XML_DECLARATION)) {
      xml.append(msg);
    }
    else if (StringUtils.startsWith(msg, STREAM_START_TAG)) {
      xml.append(msg).append(STREAM_CLOSE_TAG);
    }
    else {
      String tagName = StringUtils.substringBetween(msg, "<", " ");
      xml.append("</").append(tagName).append(">");
    }
    return null;
  }

}
