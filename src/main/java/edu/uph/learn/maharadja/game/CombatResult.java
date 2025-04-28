package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.common.DiceRoll.RollResult;

public record CombatResult(int attackerLost,
                           int defenderLost,
                           Result result,
                           RollResult rollResult) {
  public enum Result {
    OCCUPIED,
    ADVANCE,
    FORFEIT
  }
}
