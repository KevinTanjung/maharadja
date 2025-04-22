package edu.uph.learn.maharadja.utils;

import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.map.Territory;

import java.util.List;

public class TerritoryUtil {
  private TerritoryUtil() {}

  public static List<Territory> getDeployableTerritories(Player player) {
    return player.getTerritories()
        .stream()
        .filter(territory -> territory.getNumberOfStationedTroops() > 1)
        .toList();
  }
}
