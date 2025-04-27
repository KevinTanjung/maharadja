package edu.uph.learn.maharadja.game.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.player.Player;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.map.Territory;

public record TroopMovementEvent(Player actor,
                                 TurnPhase turnPhase,
                                 Territory territoryFrom,
                                 Territory territoryTo) implements Event {
}
