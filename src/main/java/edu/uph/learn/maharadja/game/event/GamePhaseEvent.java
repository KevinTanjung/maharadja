package edu.uph.learn.maharadja.game.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.game.TurnPhase;

public record GamePhaseEvent(Player currentPlayer,
                             TurnPhase phase,
                             String message) implements Event {
}
