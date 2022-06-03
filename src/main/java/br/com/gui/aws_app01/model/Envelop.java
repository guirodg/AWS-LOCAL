package br.com.gui.aws_app01.model;

import br.com.gui.aws_app01.enums.EventType;

public class Envelop {
  private EventType eventType;
  private String data;

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
