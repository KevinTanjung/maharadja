package edu.uph.learn.maharadja.ui.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.map.Territory;

import java.util.Set;

public record TerritoryHighlightedEvent(Set<Territory> territories) implements Event {
}
