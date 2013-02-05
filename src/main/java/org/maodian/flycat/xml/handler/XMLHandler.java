/**
 * 
 */
package org.maodian.flycat.xml.handler;

import javax.xml.stream.XMLStreamReader;

/**
 * @author Cole Wen
 *
 */
public interface XMLHandler {

  Object decode(XMLStreamReader reader);
}
