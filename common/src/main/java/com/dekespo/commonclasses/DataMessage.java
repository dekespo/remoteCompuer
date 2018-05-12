package com.dekespo.commonclasses;

import java.util.regex.Pattern;

public class DataMessage {
  private final String SEPARATOR = "|";
  private final Tag tag;
  private final String data;

  public DataMessage(Tag tag, String data) {
    this.tag = tag;
    this.data = data.trim();
  }

  public DataMessage(String dataMessage) {
    if (!dataMessage.contains(SEPARATOR))
      throw new RuntimeException("The message must contain the following separator: " + SEPARATOR);
    String[] keyValue = dataMessage.split(Pattern.quote(SEPARATOR));
    this.tag = Tag.valueOf(keyValue[0]);
    this.data = keyValue[1].trim();
  }

  public Tag getTag() {
    return this.tag;
  }

  public String getStringTag() {
    return this.tag.name();
  }

  public String getData() {
    return this.data;
  }

  public String getDataMessage() {
    return getStringTag() + SEPARATOR + getData();
  }

  public enum Tag {
    HANDSHAKE,
    SCREEN
  }
}
