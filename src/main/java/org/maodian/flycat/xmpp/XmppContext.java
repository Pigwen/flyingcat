/**
 * 
 */
package org.maodian.flycat.xmpp;

import io.netty.channel.ChannelHandlerContext;

import org.maodian.flycat.xmpp.OpeningStreamState.FeatureType;


/**
 * @author Cole Wen
 * 
 */
public class XmppContext {
  private final ChannelHandlerContext ctx;
  private State state;
  private String bareJID;
  private String resource;
  private String streamTag;
  
  public XmppContext(ChannelHandlerContext ctx) {
    this.ctx = ctx;
    this.state = new OpeningStreamState(FeatureType.STARTTLS);
  }

  void setState(State state) {
    this.state = state;
  }
  
  void setBareJID(String bareJID) {
    this.bareJID = bareJID;
  }
  
  void setResource(String resource) {
    this.resource = resource;
  }
  
  public void setStreamTag(String streamTag) {
    this.streamTag = streamTag;
  }

  public String getResource() {
    return resource;
  }

  public String getBareJID() {
    return bareJID;
  }
  
  ChannelHandlerContext getNettyChannelHandlerContext() {
    return ctx;
  }
  
  String wrapStreamTag(String xml) {
    return streamTag + xml;
  }

  public String parseXML(final String xml) {
    String result = state.handle(this, xml);
    return result;
  }
}
