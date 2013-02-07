/*
 * #%L
 * flyingcat
 * %%
 * Copyright (C) 2013 Ke Wen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
