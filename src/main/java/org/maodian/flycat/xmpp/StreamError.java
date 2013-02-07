package org.maodian.flycat.xmpp;

/**
 * @author Cole Wen
 *
 */
public enum StreamError implements XmppError {
  BAD_FORMAT(1, "<bad-format xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  BAD_NAMESPACE_PREFIX(2, "<bad-namespace-prefix xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  CONFLICT(3, "<conflict xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  CONNECTION_TIMEOUT(4, "<connection-timeout xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  HOST_GONE(5, "<host-gone xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  HOST_UNKNOWN(6, "<host-unknown xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  IMPROPER_ADDRESSING(7, "<improper-addressing xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  INTERNAL_SERVER_ERROR(8, "<internal-server-error xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  INVALID_FROM(9, "<invalid-from xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  INVALID_NAMESPACE(10, "<invalid-namespace xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  INVALID_XML(11, "<invalid-xml xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  NOT_AUTHORIZED(12, "<not-authorized xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  NOT_WELL_FORMED(13, "<not-well-formed xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  POLICY_VIOLATION(14, "<policy-violation xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  REMOTE_CONNECTION_FAILED(15, "<remote-connection-failed xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  RESET(16, "<reset xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  RESOURCE_CONSTRAINT(17, "<resource-constraint xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  RESTRICTED_XML(18, "<restricted-xml xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  SEE_OTHER_HOST(19, "<see-other-host xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\">[2001:41D0:1:A49b::1]:9222</see-other-host>"),
  SYSTEM_SHUTDOWN(20, "<system-shutdown xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  UNDEFINED_CONDITION(21, "<undefined-condition xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/><app-error xmlns=\"http://example.org/ns\"/>"),
  UNSUPPORTED_ENCODING(22, "<unsupported-encoding xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  UNSUPPORTED_FEATURE(23, "<unsupported-feature xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  UNSUPPORTED_STANZA_TYPE(24, "<unsupported-stanza-type xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>"),
  UNSUPPORTED_VERSION(25, "<unsupported-version xmlns=\"urn:ietf:params:xml:ns:xmpp-streams\"/>");
  
  private static final int FACTOR = 10000;
  private final int number;
  private final String xml;
  
  private StreamError(int number, String xml) {
    this.number = FACTOR + number;
    this.xml = xml;
  }

  @Override
  public int getNumber() {
    return number;
  }

  public String getXML() {
    return xml;
  }
}
