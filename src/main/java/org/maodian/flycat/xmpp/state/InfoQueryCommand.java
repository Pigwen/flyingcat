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
package org.maodian.flycat.xmpp.state;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.maodian.flycat.ApplicationContext;
import org.maodian.flycat.xmpp.InfoQuery;
import org.maodian.flycat.xmpp.StreamError;
import org.maodian.flycat.xmpp.XmppException;
import org.maodian.flycat.xmpp.XmppNamespace;
import org.maodian.flycat.xmpp.codec.Decoder;
import org.maodian.flycat.xmpp.codec.Encoder;
import org.maodian.flycat.xmpp.codec.Processor;

/**
 * @author Cole Wen
 *
 */
public class InfoQueryCommand extends ContextAwareCommand {

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.state.Command#execute(javax.xml.stream.XMLStreamReader, javax.xml.stream.XMLStreamWriter)
   */
  @Override
  public State execute(XMLStreamReader xmlsr, XMLStreamWriter xmlsw) throws XMLStreamException {
    if (!xmlsr.getName().equals(new QName(XmppNamespace.CLIENT_CONTENT, "iq"))) {
      throw new XmppException(StreamError.INVALID_NAMESPACE).set("QName", xmlsr.getName());
    }
    Decoder decoder = ApplicationContext.getInstance().getDecoder(xmlsr.getName());
    Encoder encoder = ApplicationContext.getInstance().getEncoder(InfoQuery.class);
    InfoQuery reqIQ = (InfoQuery) decoder.decode(xmlsr);
    InfoQuery.Builder iqBuilder = new InfoQuery.Builder(reqIQ.getId(), "result").from("localhost").to(reqIQ.getFrom())
        .language("en");
    Object reqPayload = reqIQ.getPayload();
    Processor processor = ApplicationContext.getInstance().getProcessor(reqPayload.getClass());
    Object rspPayload = processor.processIQ(getXmppContext(), reqIQ);
    iqBuilder.payload(rspPayload);
    encoder.encode(iqBuilder.build(), xmlsw);
    return new SelectState();
  }

}
