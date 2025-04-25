package edu.uph.learn.maharadja.game.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.game.Player;

public record DraftPhaseEvent(Player player,
                              int numOfTroops) implements Event {
}
