package edu.uph.learn.maharadja.common;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DiceRoll {
  private static final SecureRandom RANDOM = new SecureRandom();

  private DiceRoll() {
  }

  public static int roll() {
    return (int) (RANDOM.nextDouble() * 6) + 1;
  }

  public static RollResult playRoll(int attacker, int defender) {
    List<Integer> attackerRoll = new ArrayList<>();
    List<Integer> defenderRoll = new ArrayList<>();
    for (int i = 0; i < attacker; i++) {
      attackerRoll.add(roll());
    }
    attackerRoll.sort(Comparator.reverseOrder());
    for (int i = 0; i < defender; i++) {
      defenderRoll.add(roll());
    }
    defenderRoll.sort(Comparator.reverseOrder());
    return new RollResult(attackerRoll, defenderRoll);
  }

  public record RollResult(List<Integer> attackerRoll,
                           List<Integer> defenderRoll) {
  }
}
