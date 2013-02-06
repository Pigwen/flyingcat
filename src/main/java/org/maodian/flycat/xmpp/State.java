/**
 * 
 */
package org.maodian.flycat.xmpp;

/**
 * @author Cole Wen
 *
 */
public interface State {

  String handle(XmppContext context, String xml);
}
