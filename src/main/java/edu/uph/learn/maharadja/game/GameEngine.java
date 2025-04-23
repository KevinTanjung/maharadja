package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.event.AttackPhaseEvent;
import edu.uph.learn.maharadja.game.event.FortifyPhaseEvent;
import edu.uph.learn.maharadja.game.event.GamePhaseEvent;
import edu.uph.learn.maharadja.game.event.PlayerForfeitEvent;
import edu.uph.learn.maharadja.game.event.ReinforcementPhaseEvent;
import edu.uph.learn.maharadja.game.event.SkipPhaseEvent;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.utils.TerritoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static edu.uph.learn.maharadja.game.event.SkipPhaseEvent.SkipReason.NO_DEPLOYABLE_TROOP;

@SuppressWarnings("StatementWithEmptyBody")
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
    GameEngine.this.gameState.setGameMap(gameMap);
    initialDistribution();
    nextPhase();
  }

  private void initialDistribution() {
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

    //region 2. Assign remaining Troop randomly per player
    for (Player player : gameState.getPlayerList()) {
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
    int activeTurn = gameState.getPlayerList().indexOf(gameState.currentTurn());
    if (activeTurn == Constant.MAX_PLAYERS - 1) {
      gameState.setActiveTurn(0);
    } else {
      gameState.setActiveTurn(activeTurn + 1);
    }
  }

  public void nextPhase() {
    Player currentPlayer = gameState.currentTurn();
    if (gameState.currentTurn().isForfeited()) {
      EventBus.emit(new SkipPhaseEvent(currentPlayer, SkipPhaseEvent.SkipReason.PLAYER_FORFEIT));
      gameState.setActiveTurnPhase(TurnPhase.FORTIFY);
    }

    TurnPhase nextPhase = switch (gameState.getActiveTurnPhase()) {
      case START -> TurnPhase.REINFORCEMENT;
      case REINFORCEMENT -> TurnPhase.ATTACK;
      case ATTACK -> TurnPhase.FORTIFY;
      case FORTIFY -> TurnPhase.START;
    };

    gameState.setActiveTurnPhase(nextPhase);
    EventBus.emit(new GamePhaseEvent(currentPlayer, nextPhase));

    switch (nextPhase) {
      case START -> {
        checkLosingCondition(currentPlayer);
        checkWinningCondition();
        nextTurn();
      }
      case REINFORCEMENT -> reinforceTroops(currentPlayer);
      case ATTACK -> attackTroops(currentPlayer);
      case FORTIFY -> fortifyTroops(currentPlayer);
    }
  }

  private void reinforceTroops(Player currentPlayer) {
    int numOfTroops = currentPlayer.getTerritories().size() / 3;
    EventBus.emit(new ReinforcementPhaseEvent(currentPlayer, numOfTroops));
    if (currentPlayer.isComputer()) {
      // TODO: aiEngine.reinforceTroops(currentPlayer, numOfTroops);
    }
  }

  private void attackTroops(Player currentPlayer) {
    List<Territory> deployableTerritories = TerritoryUtil.getDeployableTerritories(currentPlayer);
    if (deployableTerritories.isEmpty()) {
      EventBus.emit(new SkipPhaseEvent(currentPlayer, NO_DEPLOYABLE_TROOP));
      nextPhase();
      return;
    }
    EventBus.emit(new AttackPhaseEvent(currentPlayer, deployableTerritories));
    if (currentPlayer.isComputer()) {
      // TODO: aiEngine.attackTroops()
    }
  }

  private void fortifyTroops(Player currentPlayer) {
    List<Territory> deployableTerritories = TerritoryUtil.getDeployableTerritories(currentPlayer);
    if (deployableTerritories.isEmpty()) {
      EventBus.emit(new SkipPhaseEvent(currentPlayer, NO_DEPLOYABLE_TROOP));
      nextPhase();
      return;
    }
    EventBus.emit(new FortifyPhaseEvent(currentPlayer, deployableTerritories));
    if (currentPlayer.isComputer()) {
      // TODO: aiEngine.fortifyTroops()
    }
  }

  public void occupyTerritory(Territory territory, int playerIdx, int movedTroop) {
    occupyTerritory(territory, gameState.getPlayerList().get(playerIdx), movedTroop);
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

  private void checkWinningCondition() {
    // TODO impl
  }

  private void checkLosingCondition(Player player) {
    if (player.isForfeited()) {
      return;
    }

    if (player.getTerritories().isEmpty()) {
      player.setForfeited(true);
      EventBus.emit(new PlayerForfeitEvent(player));
    }
  }
}
