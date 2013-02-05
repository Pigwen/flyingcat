package org.maodian.flycat.bean.xmpp.stream;


public class Stream {

  private final String from;
  private final String id;
  private final String to;
  private final String version;
  private final String lang;

  private Stream(Builder builder) {
    from = builder.from;
    id = builder.id;
    to = builder.to;
    version = builder.version;
    lang = builder.lang;
  }

  public String getFrom() {
    return from;
  }

  public String getId() {
    return id;
  }

  public String getTo() {
    return to;
  }

  public String getVersion() {
    return version;
  }

  public String getLang() {
    return lang;
  }

  public static class Builder {
    private String from;
    private String id;
    private String to;
    private String version;
    private String lang;

    public Builder() {
      
    }

    public Builder from(String from) {
      this.from = from;
      return this;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder to(String to) {
      this.to = to;
      return this;
    }

    public Builder version(String version) {
      this.version = version;
      return this;
    }

    public Builder lang(String lang) {
      this.lang = lang;
      return this;
    }

    public Stream build() {
      return new Stream(this);
    }
  }
}
