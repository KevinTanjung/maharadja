package edu.uph.learn.maharadja.controller;

import edu.uph.learn.maharadja.controller.common.Action;
import edu.uph.learn.maharadja.controller.common.Controller;
import edu.uph.learn.maharadja.controller.common.Message;

public class HeartbeatController implements Controller {
  @Override
  public Action getAction() {
    return Action.HEARTBEAT;
  }

  @Override
  public Message handle(Message request) {
    return new Message(Action.HEARTBEAT, "ACK");
  }
}
