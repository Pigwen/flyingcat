package org.maodian.flycat.netty.handler;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Pigwen
 *
 */
@Sharable
public class LoggerEnabledStringDecoder extends StringDecoder {

  private final Logger logger;
  
  public LoggerEnabledStringDecoder() {
    super(Charset.forName("utf-8"));
    this.logger = LoggerFactory.getLogger(getClass());
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
    String inboundMsg = ((String) super.decode(ctx, msg)).trim();
    logger.debug("Inbound String: {}", inboundMsg);
    return inboundMsg;
  }
}
