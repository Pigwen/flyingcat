/**
 * 
 */
package org.maodian.flycat.xmpp;

import org.maodian.flycat.xmpp.OpeningStreamState.FeatureType;

import io.netty.channel.ChannelHandlerContext;


/**
 * @author Cole Wen
 * 
 */
public class XmppContext {
  private final ChannelHandlerContext ctx;
  private State state;
  
  public XmppContext(ChannelHandlerContext ctx) {
    this.ctx = ctx;
    this.state = new OpeningStreamState(FeatureType.STARTTLS);
  }

  void setState(State state) {
    this.state = state;
  }
  
  ChannelHandlerContext getNettyChannelHandlerContext() {
    return ctx;
  }

  public String parseXML(final String xml) {
    String result = state.handle(this, xml);
    return result;
  }
}
