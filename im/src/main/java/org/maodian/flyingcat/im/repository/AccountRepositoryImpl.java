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
import java.util.Collections;

import org.apache.shiro.SecurityUtils;
import org.maodian.flyingcat.im.entity.Account;
import org.maodian.flyingcat.im.entity.SimpleUser;
import org.maodian.flyingcat.im.entity.SimpleUser.SubState;
import org.maodian.flyingcat.im.entity.SubscriptionRequest;
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
  public Collection<SimpleUser> getUnreadSubscription(String username) {
    String kPendingIn = Account.CONTACTS + "." + SimpleUser.PENDING_IN;
    Query query = Query.query(Criteria.where(Account.USERNAME).is(username).and(kPendingIn).is(true));
    query.fields().include(Account.CONTACTS + ".$").exclude("_id");
    Account account = getMongoTemplate().findOne(query, Account.class);
    return account == null ? Collections.EMPTY_LIST : account.getContactList();
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.repository.AccountRepositoryCustom#persistSubscriptionRequest(java.lang.String, org.maodian.flyingcat.im.entity.SubscriptionRequest)
   */
  @Override
  public void persistSubscriptionRequest(String username, SubscriptionRequest sr) {
    Query query = Query.query(Criteria.where(Account.USERNAME).is(username));
    Update update = new Update().addToSet(Account.UNREAD_REQUEST, sr);
    getMongoTemplate().updateFirst(query, update, Account.class);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.repository.AccountRepositoryCustom#getSpecificContact(java.lang.String, java.lang.String)
   */
  @Override
  public SimpleUser getSpecificContact(String uid, String targetUid) {
    String kContId = Account.CONTACTS + "." + SimpleUser.USERNAME;
    Query query = Query.query(Criteria.where(Account.USERNAME).is(uid).and(kContId).is(targetUid));
    query.fields().include(Account.CONTACTS + ".$").exclude("_id");
    Account account = getMongoTemplate().findOne(query, Account.class);
    return account == null ? null : account.getContactList().get(0);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.repository.AccountRepositoryCustom#persistContact(java.lang.String, org.maodian.flyingcat.im.entity.SimpleUser)
   */
  @Override
  public void persistContact(String uid, SimpleUser su) {
    Query query = Query.query(Criteria.where(Account.USERNAME).is(uid));
    Update update = new Update().addToSet(Account.CONTACTS, su);
    getMongoTemplate().updateFirst(query, update, Account.class);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.repository.AccountRepositoryCustom#updateContact(java.lang.String, org.maodian.flyingcat.im.entity.SimpleUser)
   */
  @Override
  public void updateContact(String uid, SimpleUser su) {
    Query query = Query.query(Criteria.where(Account.USERNAME).is(uid).and("cont.uid").is(su.getUsername()));
    Update update = new Update().set("cont.$.nick", su.getNickname()).set("cont.$.pin", su.isPendingIn())
        .set("cont.$.pout", su.isPendingOut()).set("cont.$.stat", su.getSubState().name());
    getMongoTemplate().updateFirst(query, update, Account.class);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.im.repository.AccountRepositoryCustom#getSubscribers(java.lang.String)
   */
  @Override
  public Collection<SimpleUser> getSubscribers(String uid) {
    String kState = Account.CONTACTS + "." + SimpleUser.SUB_STATE;
    Query query = Query.query(Criteria.where(Account.USERNAME).is(uid)
        .orOperator(Criteria.where(kState).is(SubState.FROM.name()), Criteria.where(kState).is(SubState.BOTH.name())));
    query.fields().include(Account.CONTACTS + ".$").exclude("_id");
    Account account = getMongoTemplate().findOne(query, Account.class); 
    return account.getContactList();
  }

}
