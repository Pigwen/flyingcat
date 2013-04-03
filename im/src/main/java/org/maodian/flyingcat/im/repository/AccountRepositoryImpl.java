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

import org.apache.shiro.SecurityUtils;
import org.maodian.flyingcat.im.entity.Account;
import org.maodian.flyingcat.im.entity.SimpleUser;
import org.maodian.flyingcat.im.entity.SimpleUser.Pending;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author Cole Wen
 * 
 */
@Repository
class AccountRepositoryImpl extends AbstractRepository implements AccountRepositoryCustom {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.maodian.flyingcat.im.repository.AccountRepositoryCustom#follow(org.
   * maodian.flyingcat.im.entity.SimpleUser)
   */
  @Override
  public void follow(SimpleUser su) {
    String username = (String) SecurityUtils.getSubject().getPrincipal();
    Query query = Query.query(Criteria.where(Account.USERNAME).is(username));
    Update update = new Update().addToSet(Account.CONTACTS, su);
    getMongoTemplate().updateFirst(query, update, Account.class);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.repository.AccountRepositoryCustom#getUnreadSubscription(org.maodian.flyingcat.im.entity.SimpleUser)
   */
  @Override
  public Collection<Account> getUnreadSubscription(SimpleUser su) {
    final String kUid = Account.CONTACTS + "." + SimpleUser.USERNAME;
    final String kPend = Account.CONTACTS + "." + SimpleUser.PENDING;
    Query query = Query.query(Criteria.where(kUid).is(su.getUsername())
        .and(kPend).is(Pending.PENDING_OUT.name()));
    query.fields().include(Account.CONTACTS).include(Account.USERNAME).exclude("_id");
    return getMongoTemplate().find(query, Account.class);
  }

}
