package edu.uph.learn.maharadja.event;

import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.player.Player;

public record RegionForfeitedEvent(Region region,
                                   Player attacker,
                                   Player previousRegent) implements Event{
}
