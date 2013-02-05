/**
 * 
 */
package org.maodian.flycat.holder;

import javax.xml.stream.XMLInputFactory;

/**
 * @author Cole Wen
 *
 */
public class XMLInputFactoryHolder {
  static final XMLInputFactory INSTANCE = createXMLInputFactory();
  
  private static final XMLInputFactory createXMLInputFactory() {
    XMLInputFactory xmlif = XMLInputFactory.newInstance();
    xmlif.setProperty("javax.xml.stream.isCoalescing", true);
    return xmlif;
  }
  
  public static final XMLInputFactory getXMLInputFactory() {
    return INSTANCE;
  }
}
