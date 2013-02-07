/**
 * 
 */
package org.maodian.flycat.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Cole Wen
 * 
 */
public class XmppMessageInboundHandler extends ChannelInboundMessageHandlerAdapter<String> {

  public static final String STREAM_START_TAG = "<stream:stream ";
  public static final String STREAM_CLOSE_TAG = "</stream:stream>";

  private XMLInputFactory xmlInputFactory;
  private StringBuilder cachedXml;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    xmlInputFactory = XMLInputFactory.newInstance();
    cachedXml = new StringBuilder(256);
    super.channelActive(ctx);
  }

  @Override
  protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
    System.out.println(msg);
    if (msg.startsWith("<?xml ")) {
      cachedXml.append(msg).append("\n");
      return;
    }

    if (msg.startsWith(STREAM_START_TAG)) {
      cachedXml.append(msg).append(STREAM_CLOSE_TAG);
      XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(cachedXml
          .toString().getBytes(StandardCharsets.UTF_8)));
      while (xmlStreamReader.hasNext()) {
        int event = xmlStreamReader.next();
        if (event == XMLStreamConstants.START_DOCUMENT) {
          System.out.println("Event Type:START_DOCUMENT");
        }
        if (event == XMLStreamConstants.START_ELEMENT) {
          System.out.println("Event Type: START_ELEMENT");
          // Output Element Local Name
          System.out.println("Element Local Name:" + xmlStreamReader.getLocalName());
          // Output Element Attributes
          for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
            System.out.println("Attribute Local Name:" + xmlStreamReader.getAttributeLocalName(i));
            System.out.println("Attribute Value:" + xmlStreamReader.getAttributeValue(i));
          }
        }
        if (event == XMLStreamConstants.CHARACTERS) {
          System.out.println("Event Type: CHARACTERS");
          System.out.println("Text:" + xmlStreamReader.getText());
        }
        if (event == XMLStreamConstants.END_DOCUMENT) {
          System.out.println("Event Type:END_DOCUMENT");
        }
        if (event == XMLStreamConstants.END_ELEMENT) {
          System.out.println("Event Type: END_ELEMENT");
        }
        System.out.println(event);
      }
      return;
    }
    
    if (msg.equals(STREAM_CLOSE_TAG)) {
      ctx.close();
    }

    throw new RuntimeException("Should not reach here");
    /*
     * XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(new
     * ByteBufInputStream(msg)); while (reader.hasNext()) { int event =
     * reader.next(); System.out.println(event); }
     */
  }

}
