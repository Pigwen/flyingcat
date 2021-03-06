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
package org.maodian.flyingcat.im.repository;

import java.util.Collection;

import org.maodian.flyingcat.im.entity.SimpleUser;
import org.maodian.flyingcat.im.entity.SubscriptionRequest;

/**
 * @author Cole Wen
 *
 */
interface AccountRepositoryCustom {
  void follow(SimpleUser su);
  
  Collection<SimpleUser> getUnreadSubscription(String username);
  
  void persistSubscriptionRequest(String username, SubscriptionRequest sr);
  
  SimpleUser getSpecificContact(String uid, String targetUid);
  
  void persistContact(String uid, SimpleUser su);
  
  void updateContact(String uid, SimpleUser su);
  
  Collection<SimpleUser> getSubscribers(String uid);
}
