package edu.uph.learn.maharadja.game.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.map.Region;

public record RegionOccupiedEvent(Region region,
                                  Player player) implements Event {
}
