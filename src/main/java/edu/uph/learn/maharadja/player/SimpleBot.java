package edu.uph.learn.maharadja.player;

import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.map.Territory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class SimpleBot extends Player implements Bot {
  private static final Logger LOG = LoggerFactory.getLogger(SimpleBot.class);
  private static final Random RANDOM = new SecureRandom();
  private static final List<Integer> chances = List.of(25, 50, 50, 75, 75, 75, 100, 100, 100, 100);

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
  public Optional<TroopMovementDecision> decideTerritoryAttack(List<Territory> deployableTerritories) {
    // 20% chance of doing nothing
    if (RANDOM.nextDouble() < 0.20) {
      return Optional.empty();
    }

    List<TroopMovementDecision> attackEvaluations = new ArrayList<>();
    for (Territory from : deployableTerritories) {
      for (Territory to : GameEngine.get().getAttackableTerritories(from)) {
        int value = 0;
        int attTroops = from.getNumberOfStationedTroops();
        int defTroops = to.getNumberOfStationedTroops();
        int defConnections = GameEngine.get().getAdjacentTerritories(to).size();

        //Prefer weaker target
        if (attTroops > defTroops) value += 3;
        else if (attTroops == defTroops) value += 1;
        else value -= 2;

        //Prefer less defender reinforcement gates, possible issue with enemy bottlenecks
        if (defConnections <= 2) value += 2;
        else if (defConnections >= 4) value -= 1;

        // Prefer nearby owned tiles
        value += GameEngine.get().getAdjacentTerritories(from)
            .stream()
            .filter(territory -> Objects.equals(territory.getOwner(), from.getOwner()))
            .toList()
            .size();
        attackEvaluations.add(new TroopMovementDecision(
            from,
            to,
            Math.max(1, (from.getNumberOfStationedTroops() - 1) * (chances.get(RANDOM.nextInt(chances.size()))) / 100),
            value
        ));
      }
    }
    logEvaluations("Attack", attackEvaluations);
    return attackEvaluations.stream()
        .max(Comparator.comparingInt(TroopMovementDecision::value))
        .filter(decision -> decision.value() > 2 || RANDOM.nextDouble() < 0.2);
  }

  @Override
  public Optional<TroopMovementDecision> decideTerritoryFortification(List<Territory> deployableTerritories) {
    // 20% chance of doing nothing
    if (RANDOM.nextDouble() < 0.20) {
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
    logEvaluations("Fortify", fortifyEvaluations);
    return fortifyEvaluations.stream().max(Comparator.comparingInt(TroopMovementDecision::value));
  }

  private static void logEvaluations(String action, List<TroopMovementDecision> evaluations) {
    LOG.info("{} Evaluations:\n{}",
        action,
        evaluations.stream()
            .sorted(Comparator.comparingInt(TroopMovementDecision::value))
            .map(d -> String.format(
                ">> Value=%2d, From=%15s, To=%15s, Orig.Troop=%3d, Depl.Troop=%3d",
                d.value(),
                d.from().getName(),
                d.to().getName(),
                d.from().getNumberOfStationedTroops(),
                d.numOfTroops()
            ))
            .collect(Collectors.joining("\n"))
    );
  }
}
