package edu.uph.learn.maharadja.ui.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.game.CombatResult;

public record CombatResultEvent(CombatResult result) implements Event {
}
