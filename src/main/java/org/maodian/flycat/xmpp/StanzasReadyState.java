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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flycat.ApplicationContext;
import org.maodian.flycat.xmpp.codec.Decoder;
import org.maodian.flycat.xmpp.codec.Encoder;
import org.maodian.flycat.xmpp.codec.Processor;

/**
 * @author Cole Wen
 *
 */
public class StanzasReadyState extends AbstractState {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.AbstractState#nextState()
   */
  @Override
  protected State nextState() {
    return this;
  }
  
  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.AbstractState#preHandle(org.maodian.flycat.xmpp.XmppContext, java.lang.String)
   */
  @Override
  protected String preHandle(XmppContext context, String xml) {
    return context.wrapStreamTag(xml);
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.AbstractState#doHandle(org.maodian.flycat.xmpp.XmppContext, javax.xml.stream.XMLStreamReader, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  protected void doHandle(XmppContext context, XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
    // skip stream tag
    xmlsr.nextTag();
    xmlsr.nextTag();
    if (!xmlsr.getName().equals(new QName(XmppNamespace.CLIENT_CONTENT, "iq"))) {
      throw new XmppException(StreamError.INVALID_NAMESPACE).set("QName", xmlsr.getName());
    }
    Decoder decoder = ApplicationContext.getInstance().getDecoder(xmlsr.getName());
    Encoder encoder = ApplicationContext.getInstance().getEncoder(InfoQuery.class);
    InfoQuery iq = (InfoQuery) decoder.decode(xmlsr);
    Processor processor = ApplicationContext.getInstance().getProcessor(iq.getPayload().getClass());
    
    InfoQuery.Builder iqBuilder = new InfoQuery.Builder(iq.getId(), "result").from("localhost").to(iq.getFrom())
        .language("en");
    Object payload = processor.process(iq.getPayload());
    iqBuilder.payload(payload);
    encoder.encode(iqBuilder.build(), xmlsw);
  }

}
