package edu.uph.learn.maharadja.game.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.game.Player;

public record SkipPhaseEvent(Player player,
                             SkipReason reason) implements Event {
  public static enum SkipReason {
    PLAYER_LOST,
    NO_DEPLOYABLE_TROOP
  }
}
