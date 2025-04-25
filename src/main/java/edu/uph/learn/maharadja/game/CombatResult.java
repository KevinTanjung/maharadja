package edu.uph.learn.maharadja.game;

public record CombatResult(int attackerLost,
                           int defenderLost,
                           Result result) {
  public enum Result {
    OCCUPIED,
    ADVANCE,
    FORFEIT
  }
}
