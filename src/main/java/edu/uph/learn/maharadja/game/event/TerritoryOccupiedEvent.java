package edu.uph.learn.maharadja.game.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.map.Territory;

public record TerritoryOccupiedEvent(Territory destination,
                                     Player player) implements Event {
}
