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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import io.netty.channel.ChannelHandlerContext;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.maodian.flyingcat.netty.handler.XMLFragmentDecoder;
import org.maodian.flyingcat.xmpp.state.StreamError;
import org.maodian.flyingcat.xmpp.state.XmppError;
import org.maodian.flyingcat.xmpp.state.XmppException;

/**
 * @author Cole Wen
 *
 */
public class XMLFragmentDecoderTest {
  private XMLFragmentDecoder decoder;
  private ChannelHandlerContext ctx;
  
  @Before
  public void setup() {
    decoder = new XMLFragmentDecoder();
    ctx = mock(ChannelHandlerContext.class);
  }

  @Test
  public void testFailsOnSingleComments() throws Exception {
    String msg = "<!-- comment -->";
    expectXmppException(decoder, msg, StreamError.RESTRICTED_XML);
  }
  
  @Test
  public void testFailsOnSingleCDATA() throws Exception {
    String msg = "<![CDATA[foobar]]>";
    expectXmppException(decoder, msg, StreamError.RESTRICTED_XML);
  }
  
  @Test
  public void testFailsOnSingleDoctype() throws Exception {
    String msg = "<!DOCTYPE journal\n" +
    		"[\n<!ELEMENT journal (article)*>\n" +
    		"<!ELEMENT article (#PCDATA)>\n" +
    		"<!ATTLIST article title CDATA #IMPLIED>\n]>";
    expectXmppException(decoder, msg, StreamError.RESTRICTED_XML);
  }
  
  @Test
  public void testFailsOnSingleEntityReference() throws Exception {
    String msg = "<!ENTITY entity_name PUBLIC \"publicId\" \"PUBLIC_URI\">";
    expectXmppException(decoder, msg, StreamError.RESTRICTED_XML);
  }
  
  @Test
  public void testFailsOnSingleProcessInstruction() throws Exception {
    String msg = "<?target \"instructions\"?>";
    expectXmppException(decoder, msg, StreamError.RESTRICTED_XML);
  }
  
  @Test
  public void testFailsOnMultipleXMLDeclaration() throws Exception {
    String msg = "<?xml version='1.0'?>";
    decoder.decode(ctx, msg);
    expectXmppException(decoder, msg, StreamError.NOT_WELL_FORMED);
  }
  
  @Test
  public void testSuccessOnSingleXMLDeclaration() throws Exception {
    String msg = "<?xml version='1.0'?>";
    String fragment = (String) decoder.decode(ctx, msg);
    assertEquals(msg, fragment);
  }
  
  @Test
  public void testSuccessOnStreamTag() throws Exception {
    String msg = "<stream:stream from='cole@localhost' to='localhost' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>";
    String fragment = (String) decoder.decode(ctx, msg);
    assertEquals(msg, fragment);
  }
  
  @Test
  public void testDecodeFirstEmptyElement() throws Exception {
    String msg = "<foo />";
    String fragment = (String) decoder.decode(ctx, msg);
    assertEquals(msg, fragment);
  }
  
  @Test
  public void testDecodeNestedEmptyElement() throws Exception {
    String[] msgs = {
        "<stream:features>",
        "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "<required/>",
        "</starttls>",
        "</stream:features>",
    };

    assertNestedElement(msgs);
  }
  
  @Test
  public void testDecodeNestedNonEmptyElement() throws Exception {
    String[] msgs = {
        "<stream:features>",
        "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "</starttls>",
        "</stream:features>",
    };
    assertNestedElement(msgs);
  }
  
  @Test
  public void testDecodeElementText() throws Exception {
    String[] msgs = {
        "<stream:features>",
        "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "<text>",
        "foobar</text>",
        "</starttls>",
        "</stream:features>",
    };
    assertNestedElement(msgs);
  }
  
  @Test
  public void testDecodeNestedElementWithText() throws Exception {
    String[] msgs1 = {
        "<stream:features>",
        "foobar<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "</starttls>",
        "</stream:features>",
    };
    assertNestedElement(msgs1);
    
    String[] msgs2 = {
        "<stream:features>",
        "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "foobar<required/>",
        "</starttls>",
        "</stream:features>",
    };
    assertNestedElement(msgs2);
  }
  
  @Test
  public void testStreamOpenTagNestesInAnotherElement() throws Exception {
    String[] msgs = {
        "<stream:features>",
        "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "<stream:stream >",
        "</starttls>",
        "</stream:features>",
    };
    expectXmppExceptionWhenProcessNestedElement(decoder, msgs, StreamError.INVALID_XML);
  }
  
  @Test
  public void testStreamOpenTagFollowsTextWhileNestesInAnotherElement() throws Exception {
    String[] msgs = {
        "<stream:features>",
        "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "foobar<stream:stream >",
        "</starttls>",
        "</stream:features>",
    };
    expectXmppExceptionWhenProcessNestedElement(decoder, msgs, StreamError.INVALID_XML);
  }
  
  @Test
  public void testStreamCloseTagNestesInAnotherElement() throws Exception {
    String[] msgs = {
        "<stream:features>",
        "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "</stream:stream>",
        "</starttls>",
        "</stream:features>",
    };
    expectXmppExceptionWhenProcessNestedElement(decoder, msgs, StreamError.INVALID_XML);
  }
  
  @Test
  public void testStreamCloseTagFollowsTextWhileNestesInAnotherElement() throws Exception {
    String[] msgs = {
        "<stream:features>",
        "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>",
        "foobar</stream:stream>",
        "</starttls>",
        "</stream:features>",
    };
    expectXmppExceptionWhenProcessNestedElement(decoder, msgs, StreamError.INVALID_XML);
  }
  
  private void assertNestedElement(String[] msgs) throws Exception {
    for (int i = 0; i < msgs.length; ++i) {
      String fragment = (String) decoder.decode(ctx, msgs[i]);
      if (i < msgs.length - 1) {
        assertNull(fragment);
      } else {
        assertEquals(StringUtils.join(msgs), fragment);
      }
    }
  }
  
  private void expectXmppException(XMLFragmentDecoder decoder, String msg, XmppError error) throws Exception {
    try {
      decoder.decode(ctx, msg);
      fail("Should not reach here");
    } catch (XmppException e) {
      assertEquals(error, e.getXmppError());
    }
  }
  
  private void expectXmppExceptionWhenProcessNestedElement(XMLFragmentDecoder decoder, String[] msgs, XmppError error) throws Exception {
    try {
      for (int i = 0; i < msgs.length; ++i) {
        decoder.decode(ctx, msgs[i]);
      }
      fail("Should not reach here");
    } catch (XmppException e) {
      assertEquals(error, e.getXmppError());
    }
  }
}
