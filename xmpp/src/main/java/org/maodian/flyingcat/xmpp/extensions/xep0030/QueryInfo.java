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
package org.maodian.flyingcat.xmpp.extensions.xep0030;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Cole Wen
 * 
 */
public class QueryInfo {
  private final List<Identity> identityList = new ArrayList<>();
  private final List<Feature> featureList = new ArrayList<>();
  private String node;

  public QueryInfo addIdentity(Identity identity) {
    identityList.add(identity);
    return this;
  }
  
  public QueryInfo addFeature(Feature feature) {
    featureList.add(feature);
    return this;
  }
  
  public List<Identity> getIdentityList() {
    return Collections.unmodifiableList(identityList);
  }

  public List<Feature> getFeatureList() {
    return Collections.unmodifiableList(featureList);
  }

  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    this.node = node;
  }

}
