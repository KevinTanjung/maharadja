package edu.uph.learn.maharadja.game.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.player.Player;
import edu.uph.learn.maharadja.map.Territory;

import java.util.List;

public record FortifyPhaseEvent(Player currentPlayer,
                                List<Territory> deployableTerritory) implements Event {
}
