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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;



/**
 * @author Cole Wen
 *
 */
public abstract class StateTest {
  protected XmppContext context;
  protected State state;
  
  @Before
  public void setup() {
    context = mock(XmppContext.class);
    doSetup();
  }
  
  public abstract void doSetup();
  
  /**
   * expect an XmppException with the desired XmppError would be thrown while the 
   * State parameter handles the xml string 
   * @param state
   * @param xml
   * @param error
   */
  protected void expectXmppException(State state, String xml, XmppError error) {
    try {
      state.step(context, xml);
      fail("Should throw an XmppException");
    } catch (XmppException e) {
      assertSame(error, e.getXmppError());
    }
  }
  
  public void testInvalidXML(State state) {
    String inXML = "invalid xml";
    expectXmppException(state, inXML, StreamError.BAD_FORMAT);
  }
}
