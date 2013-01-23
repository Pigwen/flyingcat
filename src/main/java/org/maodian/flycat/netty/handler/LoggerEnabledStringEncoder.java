/**
 * 
 */
package org.maodian.flycat.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pigwen
 * 
 */
public class LoggerEnabledStringEncoder extends StringEncoder {
  private final Logger logger;

  public LoggerEnabledStringEncoder() {
    super(Charset.forName("utf-8"));
    this.logger = LoggerFactory.getLogger(getClass());
  }
  
  @Override
  protected Object encode(ChannelHandlerContext ctx, CharSequence msg) throws Exception {
    String outboundMsg = ((ByteBuf) super.encode(ctx, msg)).toString(Charset.forName("utf-8"));
    logger.debug("Outbound String: {}", outboundMsg);
    return outboundMsg;
  }
}
