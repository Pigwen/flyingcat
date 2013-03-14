/*
 * Copyright 2013 - 2013 Cole Wen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.maodian.flyingcat.netty.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import java.lang.invoke.MethodHandles;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flyingcat.xmpp.state.StanzaError;
import org.maodian.flyingcat.xmpp.state.XmppContext;
import org.maodian.flyingcat.xmpp.state.XmppContextFactory;
import org.maodian.flyingcat.xmpp.state.XmppError;
import org.maodian.flyingcat.xmpp.state.XmppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cole Wen
 * 
 */
public class XmppXMLStreamHandler extends ChannelInboundMessageHandlerAdapter<String> {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private XmppContext xmppContext;
  private XmppContextFactory xmppContextFactory;
  
  // true if the </stream:stream> is sent first by server
  private boolean initCloseingStream = false;
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xmppContext = xmppContextFactory.newXmppContext(ctx);
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
    // discard xml declaration
    if (StringUtils.startsWith(msg, "<?xml ")) {
      return;
    }
    
    // deal with </stream:stream>
    if (StringUtils.contains(msg, ":stream") && StringUtils.contains(msg, "</")) {
      if (initCloseingStream) {
        logger.info("Close Stream and underhood socket due to requested by server");
        ctx.channel().close();
      } else {
        ctx.write("</stream:stream>").addListener(new ChannelFutureListener() {
          
          @Override
          public void operationComplete(ChannelFuture future) throws Exception {
            logger.info("Close Stream and underhood socket due to requested by client");
            future.channel().close();
          }
        });
      }
      return;
    }
    
    String result = xmppContext.parseXML(msg);
    if (StringUtils.isNotBlank(result)) {
      ctx.write(result).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      ctx.flush();
    }
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause instanceof XmppException) {
      XmppException xmppException = (XmppException) cause;
      XmppError error = xmppException.getXmppError();
      if (error instanceof StanzaError) {
        ctx.write(error.toXML()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      } else {
        xmppContext.destroy();
        StringBuilder xml = new StringBuilder(xmppException.getXmppError().toXML()).append("</stream:stream>");
        initCloseingStream = true;
        ctx.write(xml.toString()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        logger.error("Close the XMPP Stream due to error", cause);
      }
      return;
    }
    xmppContext.destroy();
    String xml = "</stream:stream>";
    initCloseingStream = true;
    ctx.write(xml).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    logger.error("Close the XMPP Stream due to error", cause);
    super.exceptionCaught(ctx, cause);
  }

  @Inject
  void setXmppContextFactory(XmppContextFactory xmppContextFactory) {
    this.xmppContextFactory = xmppContextFactory;
  }
  
}
