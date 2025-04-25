package edu.uph.learn.maharadja.ui.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.map.Territory;

public record TerritorySelectedEvent(Object eventSource,
                                     Territory territory,
                                     SelectionType type) implements Event {
  public enum SelectionType {
    FROM,
    TO
  }
}
