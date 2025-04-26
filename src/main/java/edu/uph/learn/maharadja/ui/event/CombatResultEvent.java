package edu.uph.learn.maharadja.ui.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.game.CombatResult;
import edu.uph.learn.maharadja.map.Territory;

public record CombatResultEvent(Territory attacker,
                                Territory defender,
                                CombatResult result) implements Event {
}
