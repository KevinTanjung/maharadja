package edu.uph.learn.maharadja.event;

public interface Event {
  default String getEventName() {
    return getClass().getSimpleName();
  }
}
