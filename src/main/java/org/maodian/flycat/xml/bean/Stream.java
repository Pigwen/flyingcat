/**
 * 
 */
package org.maodian.flycat.xml.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Cole Wen
 *
 */
@XmlRootElement(namespace = Stream.STREAM_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class Stream {
  
  public static final String STREAM_NAMESPACE = "http://etherx.jabber.org/streams";
  public static final String STREAM_DEFAULT_NAMESPACE = "jabber:client";
}
