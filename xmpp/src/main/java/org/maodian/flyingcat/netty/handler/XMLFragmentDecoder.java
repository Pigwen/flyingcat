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

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.lang.invoke.MethodHandles;

import org.apache.commons.lang3.StringUtils;
import org.maodian.flyingcat.xmpp.state.StreamError;
import org.maodian.flyingcat.xmpp.state.XmppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cole Wen
 *
 */
public class XMLFragmentDecoder extends MessageToMessageDecoder<String> {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String[] INVALID_ELEMENT_HEADS = {
    "<!", //DOCTYPE and ENTITY reference
    };
  private static final String[] INVALID_ELEMENT_TAILS = {
    "-->",  // comments 
    "]]>",  // CDATA
    };
  private static final String PROCESS_INSTRUCTION_TAIL = "?>";   // processing instructions"
  
  private int depth = 0;
  private StringBuilder xml;
  private boolean acceptXMLDeclaration = true;
  
  /* (non-Javadoc)
   * @see io.netty.handler.codec.MessageToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, java.lang.Object)
   */
  @Override
  protected Object decode(ChannelHandlerContext ctx, String msg) throws Exception {
    if (StringUtils.endsWithAny(msg, INVALID_ELEMENT_TAILS)) {
      throw new XmppException(StreamError.RESTRICTED_XML).set("xml", msg);
    }
    
    if (StringUtils.endsWith(msg, PROCESS_INSTRUCTION_TAIL) && !StringUtils.startsWith(msg, "<?xml ")) {
      throw new XmppException(StreamError.RESTRICTED_XML).set("xml", msg);
    }
    
    if (StringUtils.startsWithAny(msg, INVALID_ELEMENT_HEADS)) {
      throw new XmppException(StreamError.RESTRICTED_XML).set("xml", msg);
    }
    
    // only accept xml declaration once
    if (StringUtils.startsWith(msg, "<?xml ")) {
      if (acceptXMLDeclaration) {
        acceptXMLDeclaration = false;
        return msg;
      } else {
        throw new XmppException("XML declaration has been already received before", StreamError.NOT_WELL_FORMED).set("xml", msg);
      }
    }
    
    // deal with stream tag
    if (StringUtils.contains(msg, ":stream") && 
        (StringUtils.contains(msg, "<") || StringUtils.contains(msg, "</"))){
      if (depth != 0) {
        throw new XmppException("Stream Open/Close Tag can only be root element", StreamError.INVALID_XML);
      }
      return msg;
    }
    
    // deal with empty element at first level
    if (depth == 0 && StringUtils.endsWith(msg, "/>")) {
      return msg;
    }
    
    if (xml == null) {
      xml = new StringBuilder(msg);
    } else {
      xml.append(msg);
    }
    
    // deal with nested empty element
    if (StringUtils.endsWith(msg, "/>")) {
      return null;
    }

    if (StringUtils.contains(msg, "</")) {
      depth--;
    } else if (StringUtils.contains(msg, "<")) {
      depth++;
    }

    if (depth == 0) {
      String fragment = xml.toString();
      xml = null;
      return fragment;
    }
    return null;
  }

  /* (non-Javadoc)
   * @see io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause instanceof XmppException) {
      XmppException xmppException = (XmppException) cause;
      StringBuilder xml = new StringBuilder(xmppException.getXmppError().toXML()).append("</stream:stream>");
      ctx.write(xml.toString()).addListener(ChannelFutureListener.CLOSE);
      logger.error("Close the XMPP Stream due to error", cause);
    }
    super.exceptionCaught(ctx, cause);
  }
}
