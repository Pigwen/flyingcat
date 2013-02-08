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
package org.maodian.flycat.netty.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flycat.xmpp.XmppContext;

/**
 * @author Cole Wen
 * 
 */
public class StreamElementExtractHandler extends ChannelInboundMessageHandlerAdapter<String> {
  private static final String XML_DECLARATION_MARK = "<?xml ";

  private boolean recvXmlDeclFlag = false;
  private XmppContext xmppContext;
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xmppContext = new XmppContext(ctx);
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
        recvXmlDeclFlag = true;
        return;
      }
      // TODO: deal with duplicated xml declaration
      throw new RuntimeException("deal with duplicated xml declaration");
    }
    
    String result = xmppContext.parseXML(msg);
    if (StringUtils.isNotBlank(result)) {
      ctx.write(result).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      ctx.flush();
    }
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
  }
}
