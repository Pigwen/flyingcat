/**
 * 
 */
package org.maodian.flycat;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.nio.charset.Charset;

import org.maodian.flycat.netty.handler.LoggerEnabledStringDecoder;
import org.maodian.flycat.netty.handler.LoggerEnabledStringEncoder;
import org.maodian.flycat.netty.handler.StreamElementExtractHandler;

/**
 * @author Cole Wen
 *
 */
public class XmppServerInitializer extends ChannelInitializer<SocketChannel> implements ChannelHandler {
  private static final LoggerEnabledStringDecoder DECODER = new LoggerEnabledStringDecoder();
  private static final LoggerEnabledStringEncoder ENCODER = new LoggerEnabledStringEncoder();
  
  /* (non-Javadoc)
   * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
   */
  @Override
  public void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    
    String delimiter = ">";//XmppMessageInboundHandler.STREAM_NAME + ">";
    p.addLast("Frame", new DelimiterBasedFrameDecoder(8192, false, true, Unpooled.wrappedBuffer(delimiter.getBytes(Charset.forName("utf-8")))));
    p.addLast("Logger", DECODER);
    p.addLast("Encoder", ENCODER);
//    p.addLast("Echo", new XmppMessageInboundHandler());
    //p.addLast("Echo", new StreamNegotiationHandler());
    p.addLast("Echo", new StreamElementExtractHandler());
  }

}
