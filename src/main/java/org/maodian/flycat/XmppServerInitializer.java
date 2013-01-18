/**
 * 
 */
package org.maodian.flycat;

import java.nio.charset.Charset;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import org.maodian.flycat.netty.handler.XmppMessageInboundHandler;

/**
 * @author Cole Wen
 *
 */
public class XmppServerInitializer extends ChannelInitializer<SocketChannel> implements ChannelHandler {
  private static final StringDecoder DECODER = new StringDecoder(Charset.forName("UTF-8"));
  
  /* (non-Javadoc)
   * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
   */
  @Override
  public void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    
    String delimiter = ">";//XmppMessageInboundHandler.STREAM_NAME + ">";
    p.addLast("frame", new DelimiterBasedFrameDecoder(8192, false, true, Unpooled.wrappedBuffer(delimiter.getBytes(Charset.forName("utf-8")))));
    p.addLast("Decoder", DECODER);
    p.addLast("Echo", new XmppMessageInboundHandler());
  }

}
