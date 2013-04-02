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
package org.maodian.flyingcat.im.template;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.maodian.flyingcat.im.Type;
import org.maodian.flyingcat.im.Verb;
import org.maodian.flyingcat.im.entity.Account;
import org.maodian.flyingcat.im.entity.SimpleUser;
import org.maodian.flyingcat.im.entity.SimpleUser.Pending;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author Cole Wen
 * 
 */
@Service
@Domain(Type.CONTACT)
public class SubscriptionTemplate extends AbstractTemplate {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Operation(Verb.RETRIEVE)
  public List<Account> retrieve(final SimpleUser su) {
    final String kUid = Account.CONTACTS + "." + SimpleUser.USERNAME;
    final String kPend = Account.CONTACTS + "." + SimpleUser.PENDING;
    Query query = Query.query(Criteria.where(kUid).is(su.getUsername())
        .and(kPend).is(Pending.PENDING_OUT.name()));
    query.fields().include(Account.CONTACTS).include(Account.USERNAME).exclude("_id");
    /*return getMongoTemplate().execute(Account.COLLECTION_NAME, new CollectionCallback<Collection<SimpleUser>>() {

      @Override
      public Collection<SimpleUser> doInCollection(DBCollection collection) throws MongoException, DataAccessException {
        BasicDBObject query = new BasicDBObject(kUid, su.getUsername()).append(kPend, Pending.PENDING_OUT.name());
        BasicDBObject fields = new BasicDBObject(Account.CONTACTS, 1).append("_id", 0);
        DBCursor cursor = collection.find(query, fields);
        List<SimpleUser> unreadSubscription = new ArrayList<>();
        try {
          while (cursor.hasNext()) {
            DBObject dbObj = cursor.next();
            Collection<DBObject> subObjs = (Collection<DBObject>) dbObj.get(Account.CONTACTS);
            for (DBObject o : subObjs) {
              String u = (String) o.get(SimpleUser.USERNAME);
              String n = (String) o.get(SimpleUser.NICK);
              String s = (String) o.get(SimpleUser.SUB_STATE);
              String p = (String) o.get(SimpleUser.PENDING);
              SimpleUser sub = new SimpleUser(u, n);
              sub.setPending(Pending.fromString(p));
              sub.setSubState(SubState.fromString(s));
              unreadSubscription.add(sub);
            }
          }
        } finally {
          cursor.close();
        }
        return unreadSubscription;
      }
    });*/
    return getMongoTemplate().find(query, Account.class);
  }
}
