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
package org.maodian.flycat.xmpp;

import org.maodian.flycat.xmpp.OpeningStreamState.FeatureType;

/**
 * Utility class which contains factory methods for creating {@link State} instance.
 * 
 * @author Cole Wen
 *
 */
public class States {

  private States() {
    
  }
  
  public static State newOpeningStreamState(FeatureType featureType) {
    return new OpeningStreamState(featureType);
  }
  
  public static State newSASLState() {
    return new SASLState();
  }
  
  public static State newStartTLSState() {
    return new StartTLSState();
  }
  
  public static State newResourceBindState() {
    return new ResourceBindState();
  }
  
  public static State newStanzasReadyState() {
    return new StanzasReadyState();
  }
}
