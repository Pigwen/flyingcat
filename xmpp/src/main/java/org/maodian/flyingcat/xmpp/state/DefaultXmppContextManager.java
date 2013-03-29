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

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import org.maodian.flyingcat.xmpp.entity.JabberID;


/**
 * @author Cole Wen
 *
 */
public class DefaultXmppContextManager implements XmppContextManager {
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
    try {
      lock.acquire();
      ConcurrentMap<JabberID, XmppContext> m = pool.get(jid.getUid());
      m.remove(jid);
      if (m.size() == 0) {
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
    try {
      lock.acquire();
      ConcurrentMap<JabberID, XmppContext> m = pool.get(jid.getUid());
      if (m == null) {
        m = new ConcurrentHashMap<>();
        pool.put(jid.getUid(), m);
      }
      m.put(jid, ctx);
    } catch (InterruptedException e) {
      throw new RuntimeException();
    } finally {
      lock.release();
    }
  }
  
  
}
