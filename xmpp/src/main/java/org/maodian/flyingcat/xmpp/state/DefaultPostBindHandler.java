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
package org.maodian.flyingcat.xmpp.state;

import java.util.List;

import javax.xml.stream.XMLStreamException;

/**
 * @author Cole Wen
 * 
 */
public class DefaultPostBindHandler implements PostBindHandler, PipelineManager<XmppContext> {
  private List<Pipeline<XmppContext>> pipelineHandlerList;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.xmpp.state.PostBindHandler#handle(org.maodian.flyingcat
   * .xmpp.state.XmppContext)
   */
  @Override
  public void handle(XmppContext ctx) {
    doPipeline(ctx);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.xmpp.state.PipelineManager#doPipeline(java.lang.Object
   * )
   */
  @Override
  public void doPipeline(XmppContext cmd) {
    try {
      for (Pipeline<XmppContext> p : pipelineHandlerList) {
        p.process(cmd);
      }
    } catch (XMLStreamException e) {
      throw new RuntimeException();
    }
  }

  public void setPipelineHandlerList(List<Pipeline<XmppContext>> pipelineHandlerList) {
    this.pipelineHandlerList = pipelineHandlerList;
  }

}
