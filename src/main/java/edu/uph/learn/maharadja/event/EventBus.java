package edu.uph.learn.maharadja.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
  private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

  private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listenerMap;

  //region Singleton
  private EventBus() {
    this.listenerMap = new HashMap<>();
  }

  private static class EventBusHolder {
    private static EventBus INSTANCE;
  }

  public static EventBus init() {
    EventBusHolder.INSTANCE = new EventBus();
    return EventBusHolder.INSTANCE;
  }
  //endregion

  public static <T extends Event> void registerListener(
      Class<T> event,
      EventListener<T> listener
  ) {
    EventBusHolder.INSTANCE.listenerMap.putIfAbsent(event, new ArrayList<>());
    EventBusHolder.INSTANCE.listenerMap.get(event).add(listener);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Event> void emit(T event) {
    List<EventListener<? extends Event>> listeners = EventBusHolder.INSTANCE.listenerMap.getOrDefault(
        event.getClass(),
        List.of()
    );
    LOG.info("Emitted Event: {}, invoking {} listeners", event.getEventName(), listeners.size());
    for (EventListener<? extends Event> listener : listeners) {
      ((EventListener<T>) listener).onEvent(event);
    }
  }
}
