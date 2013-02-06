/**
 * 
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
    ctx.write(result).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    ctx.flush();
  }
  
}
