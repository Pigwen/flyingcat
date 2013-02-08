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
package org.maodian.flycat.xmpp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.net.ssl.SSLEngine;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.maodian.flycat.holder.XMLInputFactoryHolder;
import org.maodian.flycat.xmpp.OpeningStreamState.FeatureType;

/**
 * @author Cole Wen
 *
 */
public class StartTLSState implements State {
  
  private static final String PROCEED_CMD = "<proceed xmlns=\"" + XmppNamespace.TLS + "\"/>";

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.State#handle(org.maodian.flycat.xmpp.XmppContext, java.lang.String)
   */
  @Override
  public String handle(XmppContext context, String xml) {
    try (Reader reader = new StringReader(xml)) {
      try {
        XMLStreamReader xmlsr = XMLInputFactoryHolder.getXMLInputFactory().createXMLStreamReader(reader);
        xmlsr.nextTag();
        QName qname = new QName(XmppNamespace.TLS, "starttls");
        if (!xmlsr.getName().equals(qname)) {
          throw new XmppException(StreamError.INVALID_NAMESPACE)
              .set("QName", qname);
        }
        
        ChannelHandlerContext ctx = context.getNettyChannelHandlerContext();
        SSLEngine engine = SecureSslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);
        ctx.pipeline().addFirst("ssl", new SslHandler(engine, true));
        
        // set state back to OpeningStream state since client would start a new stream
        context.setState(new OpeningStreamState(FeatureType.SASL));
        return PROCEED_CMD;
      } catch (XMLStreamException e) {
        throw new XmppException(e, StreamError.BAD_FORMAT);
      }
    } catch (IOException ioe) {
      // close a StringReader/StringWriter should not cause IOException, though
      throw new XmppException(ioe, StreamError.INTERNAL_SERVER_ERROR);
    }
  }

}
