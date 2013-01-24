/**
 * 
 */
/**
 * @author Cole Wen
 *
 */
@XmlSchema(namespace = XmppNamespace.STREAM, elementFormDefault = XmlNsForm.QUALIFIED,
    xmlns = {
        @XmlNs(namespaceURI = XmppNamespace.STREAM, prefix = "stream"),
        @XmlNs(namespaceURI = XmppNamespace.CONTENT, prefix = "")
  }
)
package org.jabber.etherx.streams;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlNs;
import org.maodian.flycat.xmpp.XmppNamespace;