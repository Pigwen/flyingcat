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
package org.maodian.flyingcat.xmpp.state;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import org.maodian.flyingcat.xmpp.entity.JabberID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Cole Wen
 *
 */
public class DefaultXmppContextManager implements XmppContextManager {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final ConcurrentMap<String, ConcurrentMap<JabberID, XmppContext>> pool = new ConcurrentHashMap<>();
  private final Semaphore lock = new Semaphore(1);
  
  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.XmppContextManager#getXmppContext(org.maodian.flyingcat.xmpp.entity.JabberID)
   */
  @Override
  public XmppContext getXmppContext(JabberID jid) {
    return pool.get(jid.getUid()).get(jid);
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.XmppContextManager#getXmppContexts(java.lang.String)
   */
  @Override
  public Collection<XmppContext> getXmppContexts(String uid) {
    return pool.get(uid).values();
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.XmppContextListener#onPreDestroy(org.maodian.flyingcat.xmpp.state.XmppContext)
   */
  @Override
  public void onPreDestroy(XmppContext ctx) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.XmppContextListener#onPostDestroy(org.maodian.flyingcat.xmpp.state.XmppContext)
   */
  @Override
  public void onPostDestroy(XmppContext ctx) {
    JabberID jid = ctx.getJabberID();
    log.debug("Post destroy of XmppContext for {}", jid);
    try {
      lock.acquire();
      ConcurrentMap<JabberID, XmppContext> m = pool.get(jid.getUid());
      m.remove(jid);
      if (m.size() == 0) {
        log.debug("No active XmppContext for uid {} anymore", jid.getUid());
        pool.remove(jid.getUid());
      }
    } catch (InterruptedException e) {
      throw new RuntimeException();
    } finally {
      lock.release();
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.XmppContextListener#onPreLogin(org.maodian.flyingcat.xmpp.state.XmppContext)
   */
  @Override
  public void onPreLogin(XmppContext ctx) {
    
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.XmppContextListener#onPostLogin(org.maodian.flyingcat.xmpp.state.XmppContext)
   */
  @Override
  public void onPostLogin(XmppContext ctx) {
    JabberID jid = ctx.getJabberID();
    log.debug("Post login of XmppContext for {}", jid);
    try {
      lock.acquire();
      ConcurrentMap<JabberID, XmppContext> m = pool.get(jid.getUid());
      if (m == null) {
        log.debug("No active XmppContext found for uid {}", jid.getUid());
        m = new ConcurrentHashMap<>();
        pool.put(jid.getUid(), m);
      }
      m.put(jid, ctx);
      log.debug("Complete post login of XmppContext");
    } catch (InterruptedException e) {
      throw new RuntimeException();
    } finally {
      lock.release();
    }
  }

  /* (non-Javadoc)
   * @see org.maodian.flyingcat.xmpp.state.XmppContextManager#transfer(org.maodian.flyingcat.xmpp.entity.JabberID, org.maodian.flyingcat.xmpp.entity.JabberID, java.lang.Object)
   */
  @Override
  public void transfer(JabberID from, JabberID to, Object payload) {
    ConcurrentMap<JabberID, XmppContext> m = pool.get(to.getUid());
    if (m == null) {
      //TODO: the target is not active, store the payload in db
    } else {
      Collection<XmppContext> ctxs = m.values();
      for (XmppContext ctx : ctxs) {
        ctx.receive(from, payload);
      }
    }
    
  }
}
