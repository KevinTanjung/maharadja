package edu.uph.learn.maharadja.player;

import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.map.Territory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class SimpleBot extends Player implements Bot {
  private static final Random RANDOM = new SecureRandom();
  private static final List<Integer> chances = List.of(25, 50, 75, 100);

  public SimpleBot(int i) {
    super("Computer " + i);
    computer = true;
    setColor(PLAYER_COLORS.get(i-1));
  }

  @Override
  public Map<Territory, Integer> decideTroopDraft(int numOfTroops) {
    Map<Territory, Integer> draftTroops = new HashMap<>();
    int remainingTroops = numOfTroops;
    for (Territory territory : getTerritories()) {
      if (remainingTroops <= 0) break;
      draftTroops.putIfAbsent(territory, 0);
      draftTroops.put(territory, draftTroops.get(territory) + 1);
      remainingTroops--;
    }
    return draftTroops;
  }

  @Override
  public Optional<TroopMovementDecision> decideTerritoryFortification(List<Territory> deployableTerritories) {
    // 5% chance of doing nothing
    if (RANDOM.nextDouble() < 0.05) {
      return Optional.empty();
    }

    List<TroopMovementDecision> fortifyEvaluations = new ArrayList<>();
    for (Territory from : deployableTerritories) {
      List<Territory> fortifiableTerritories = GameEngine.get().getFortifiableTerritories(from);
      for (Territory to : fortifiableTerritories) {
        int value = 0;
        // prefer fortify loose defences
        if (to.getNumberOfStationedTroops() < 3) value += 2;
        else if (to.getNumberOfStationedTroops() < from.getNumberOfStationedTroops()) value += 1;
        // prefer fortify frontiers
        value += 2 * GameEngine.get().getAttackableTerritories(to).size();
        // prefer concentrated hold
        value += GameEngine.get().getFortifiableTerritories(to).size();
        fortifyEvaluations.add(new TroopMovementDecision(
            from,
            to,
            Math.max(1, (from.getNumberOfStationedTroops() - 1) * (chances.get(RANDOM.nextInt(chances.size()))) / 100),
            value
        ));
      }
    }
    return fortifyEvaluations.stream().max(Comparator.comparingInt(TroopMovementDecision::value));
  }
}
