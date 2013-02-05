/**
 * 
 */
package org.maodian.flycat.holder;

import javax.xml.stream.XMLOutputFactory;

/**
 * @author Cole Wen
 *
 */
public class XMLOutputFactoryHolder {
  static final XMLOutputFactory INSTANCE = createXMLOutputFactory();
  
  private static final XMLOutputFactory createXMLOutputFactory() {
    return XMLOutputFactory.newInstance();
  }
  
  public static final XMLOutputFactory getXMLOutputFactory() {
    return INSTANCE;
  }
}
