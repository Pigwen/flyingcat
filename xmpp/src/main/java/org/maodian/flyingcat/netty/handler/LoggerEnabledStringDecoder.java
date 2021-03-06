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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Cole Wen
 *
 */
@Sharable
public class LoggerEnabledStringDecoder extends StringDecoder {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  
  public LoggerEnabledStringDecoder() {
    super(StandardCharsets.UTF_8);
  }

  @Override
  protected Object decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
    String inboundMsg = ((String) super.decode(ctx, msg)).trim();
    logger.debug("Inbound String: {}", inboundMsg);
    return inboundMsg;
  }
}
