package edu.uph.learn.maharadja.controller.common;

public interface Controller {
  Action getAction();

  Message handle(Message request);
}
