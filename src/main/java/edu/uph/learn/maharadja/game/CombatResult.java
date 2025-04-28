package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.common.DiceRoll.RollResult;
import edu.uph.learn.maharadja.player.Player;

public record CombatResult(int attackerLost,
                           int defenderLost,
                           Result result,
                           RollResult rollResult,
                           Player attacker,
                           Player defender) {
  public enum Result {
    TERRITORY_OCCUPIED,
    REGION_OCCUPIED,
    REGION_FORFEITED,
    ADVANCE,
    FORFEIT;

    public boolean conquered() {
      return this == REGION_OCCUPIED || this == REGION_FORFEITED || this == TERRITORY_OCCUPIED;
    }
  }
}
