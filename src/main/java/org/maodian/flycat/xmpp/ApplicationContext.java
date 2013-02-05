/**
 * 
 */
package org.maodian.flycat.xmpp;

import javax.xml.namespace.QName;

import org.maodian.flycat.xml.handler.StreamHandler;
import org.maodian.flycat.xml.handler.XMLHandler;

/**
 * @author Cole Wen
 *
 */
public class ApplicationContext {

  private static final ApplicationContext INSTANCE = new ApplicationContext();
  
  private ApplicationContext() {
    
  }
  
  public XMLHandler getXMLHandler(QName key) {
    return new StreamHandler();
  }
  
  public static ApplicationContext getInstance() {
    return INSTANCE;
  }
}
