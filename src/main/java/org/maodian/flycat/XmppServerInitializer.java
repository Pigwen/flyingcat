/*
 * #%L
 * flyingcat
 * %%
 * Copyright (C) 2013 Ke Wen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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

import java.nio.charset.StandardCharsets;

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
    p.addLast("Frame", new DelimiterBasedFrameDecoder(8192, false, true, Unpooled.wrappedBuffer(delimiter.getBytes(StandardCharsets.UTF_8))));
    p.addLast("Logger", DECODER);
    p.addLast("Encoder", ENCODER);
//    p.addLast("Echo", new XmppMessageInboundHandler());
    //p.addLast("Echo", new StreamNegotiationHandler());
    p.addLast("Echo", new StreamElementExtractHandler());
  }
}
