package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.Territory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameEngine {
  private static final Random RANDOM = new SecureRandom();
  private static final Logger log = LoggerFactory.getLogger(GameEngine.class);
  private final GameMap gameMap;
  private final GameState gameState;

  public GameEngine(GameMap gameMap) {
    this(gameMap, GameState.get());
  }

  public GameEngine(GameMap gameMap, GameState gameState) {
    this.gameMap = gameMap;
    this.gameState = gameState;
    getGameState().setGameMap(gameMap);
    assignTiles();
    getGameState().setActiveTurnPhase(TurnPhase.REINFORCEMENT);
  }

  private void assignTiles() {
    Set<Territory> visitedTerritories = new HashSet<>();
    List<Territory> territories = gameMap.getAllTerritories();
    int territoryCount = territories.size();
    int playerCount = 0;

    if (territoryCount == 0) {
      return;
    }

    //region 1. Assign Territory randomly and place a single troop
    while (visitedTerritories.size() != territoryCount) {
      Territory territory = territories.get(RANDOM.nextInt(territoryCount));
      while (visitedTerritories.contains(territory)) {
        territory = territories.get(RANDOM.nextInt(territoryCount));
      }

      occupyTerritory(territory, playerCount, 0);
      visitedTerritories.add(territory);

      if (playerCount == Constant.MAX_PLAYERS - 1) {
        playerCount = 0;
      } else {
        playerCount++;
      }
    }
    //endregion

    //region 2. Assign remaining Troop randomly per playe
    for (Player player : getGameState().getPlayerList()) {
      List<Territory> occupiedTerritories = new ArrayList<>(player.getTerritories());
      // since initially set to 1, number of territory equals the number of troops
      int troopCount = occupiedTerritories.size();
      while (troopCount < Constant.INITIAL_TROOP_PER_PLAYER) {
        Territory territory = occupiedTerritories.get(RANDOM.nextInt(occupiedTerritories.size()));
        territory.deployTroop(1);
        troopCount++;

        log.info(
            "[Player {}] deployed [1 troop] to territory [{}/{}], now has [{} troop].",
            player.getUsername(), territory.getName(), territory.getRegion().getName(), territory.getNumberOfStationedTroops()
        );
      }
    }
    //endregion
  }

  private void nextTurn() {
    int activeTurn = getGameState().getPlayerList().indexOf(getGameState().currentTurn());
    if (activeTurn == Constant.MAX_PLAYERS - 1) {
      getGameState().setActiveTurn(0);
    } else {
      getGameState().setActiveTurn(activeTurn + 1);
    }
  }

  public void nextPhase() {
    switch (getGameState().getActiveTurnPhase()) {
      case REINFORCEMENT -> gameState.setActiveTurnPhase(TurnPhase.ATTACK);
      case ATTACK -> gameState.setActiveTurnPhase(TurnPhase.FORTIFY);
      case FORTIFY -> {
        gameState.setActiveTurnPhase(TurnPhase.REINFORCEMENT);
        nextTurn();
      }
    }
  }

  public void occupyTerritory(Territory territory, int playerIdx, int movedTroop) {
    occupyTerritory(territory, getGameState().getPlayerList().get(playerIdx), movedTroop);
  }

  public void occupyTerritory(Territory territory, Player player, int movedTroop) {
    territory.setOwner(player);
    territory.deployTroop(movedTroop);
    player.addTerritory(territory);

    log.info(
        "[Player {}] occupied the territory [{}/{}] and deployed [{} troop], now has [{} troop].",
        player.getUsername(), territory.getName(), territory.getRegion().getName(), movedTroop, territory.getNumberOfStationedTroops()
    );
  }

  private GameState getGameState() {
    return gameState;
  }
}
