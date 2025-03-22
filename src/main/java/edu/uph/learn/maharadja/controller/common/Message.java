package edu.uph.learn.maharadja.controller.common;

public class Message {
  private final Action action;
  private final String payload;

  public Message(Action action, String payload) {
    this.action = action;
    this.payload = payload;
  }

  public Action getAction() {
    return action;
  }

  public String getPayload() {
    return payload;
  }
}
