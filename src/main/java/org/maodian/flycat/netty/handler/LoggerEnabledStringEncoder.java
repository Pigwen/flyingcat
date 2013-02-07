/**
 * 
 */
package org.maodian.flycat.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pigwen
 * 
 */
public class LoggerEnabledStringEncoder extends StringEncoder {
  private final Logger logger;

  public LoggerEnabledStringEncoder() {
    super(StandardCharsets.UTF_8);
    this.logger = LoggerFactory.getLogger(getClass());
  }
  
  @Override
  protected Object encode(ChannelHandlerContext ctx, CharSequence msg) throws Exception {
    Object object = super.encode(ctx, msg);
    logger.debug("Outbound String: {}", ((ByteBuf) object).toString(StandardCharsets.UTF_8));
    return object;
  }
}
