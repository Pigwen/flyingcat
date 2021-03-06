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

/**
 * @author Cole Wen
 *
 */
public interface XmppContextListener {

  void onPreDestroy(XmppContext ctx);
  void onPostDestroy(XmppContext ctx);
  void onPreLogin(XmppContext ctx);
  void onPostLogin(XmppContext ctx);
  void onPreBind(XmppContext ctx);
  void onPostBind(XmppContext ctx);
  void onPreSend(XmppContext ctx, Object payload);
  void onPostSend(XmppContext ctx, Object payload);
  void onPreReceive(XmppContext ctx, Object payload);
  void onPostReceive(XmppContext ctx, Object payload);
}
