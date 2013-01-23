/**
 * 
 */
package org.maodian.flycat.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundMessageHandlerAdapter;

/**
 * @author Pigwen
 *
 */
public class OutboundMessageLoggerHandler extends ChannelOutboundMessageHandlerAdapter<String> {

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    // TODO Auto-generated method stub
    super.userEventTriggered(ctx, evt);
  }

}
