package edu.uph.learn.maharadja.event;

@FunctionalInterface
public interface EventListener<T extends Event> {
  void onEvent(T event);
}
