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
package org.maodian.flycat.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pigwen
 * 
 */
public class LoggerEnabledStringEncoder extends StringEncoder {
  private final Logger logger;

  public LoggerEnabledStringEncoder() {
    super(StandardCharsets.UTF_8);
    this.logger = LoggerFactory.getLogger(getClass());
  }
  
  @Override
  protected Object encode(ChannelHandlerContext ctx, CharSequence msg) throws Exception {
    Object object = super.encode(ctx, msg);
    logger.debug("Outbound String: {}", ((ByteBuf) object).toString(StandardCharsets.UTF_8));
    return object;
  }
}
