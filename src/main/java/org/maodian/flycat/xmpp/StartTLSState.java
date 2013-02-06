/**
 * 
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
          //TODO: Deal with invalid stream
          throw new RuntimeException("Deal with invalid stream");
        }
        
        ChannelHandlerContext ctx = context.getNettyChannelHandlerContext();
        SSLEngine engine = SecureSslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);
        ctx.pipeline().addFirst("ssl", new SslHandler(engine, true));
        
        // set state back to OpeningStream state since client would start a new stream
        context.setState(new OpeningStreamState(false));
        return PROCEED_CMD;
      } catch (XMLStreamException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

}
