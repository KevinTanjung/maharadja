package edu.uph.learn.maharadja.game.event;

import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.player.Player;

public record SkipPhaseEvent(Player player,
                             SkipReason reason) implements Event {
  public enum SkipReason {
    PLAYER_FORFEIT,
    NO_DEPLOYABLE_TROOP
  }
}
