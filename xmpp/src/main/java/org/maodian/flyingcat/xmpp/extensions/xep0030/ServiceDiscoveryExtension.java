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
package org.maodian.flyingcat.xmpp.extensions.xep0030;

import javax.xml.namespace.QName;

import org.maodian.flyingcat.xmpp.ApplicationContext;
import org.maodian.flyingcat.xmpp.Extension;

/**
 * @author Cole Wen
 *
 */
public class ServiceDiscoveryExtension implements Extension {

  /* (non-Javadoc)
   * @see org.maodian.flycat.Extension#register(org.maodian.flycat.ApplicationContext)
   */
  @Override
  public void register(ApplicationContext ctx) {
    QueryInfoCodec queryInfoCodec = new QueryInfoCodec(ctx);
    QueryItemCodec queryItemCodec = new QueryItemCodec(ctx);
    
    ctx.registerDecoder(new QName(ServiceDiscovery.INFORMATION, "query"), queryInfoCodec);
    ctx.registerDecoder(new QName(ServiceDiscovery.ITEM, "query"), queryItemCodec);
    
    ctx.registerEncoder(QueryInfo.class, queryInfoCodec);
    ctx.registerEncoder(QueryItem.class, queryItemCodec);
    
    ctx.registerProcessor(QueryInfo.class, queryInfoCodec);
    ctx.registerProcessor(QueryItem.class, queryItemCodec);
  }

}
