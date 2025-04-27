package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.common.DiceRoll;
import edu.uph.learn.maharadja.common.TerritoryUtil;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.event.AttackPhaseEvent;
import edu.uph.learn.maharadja.game.event.DraftPhaseEvent;
import edu.uph.learn.maharadja.game.event.FortifyPhaseEvent;
import edu.uph.learn.maharadja.game.event.GamePhaseEvent;
import edu.uph.learn.maharadja.game.event.PlayerForfeitEvent;
import edu.uph.learn.maharadja.game.event.RegionOccupiedEvent;
import edu.uph.learn.maharadja.game.event.SkipPhaseEvent;
import edu.uph.learn.maharadja.game.event.TerritoryOccupiedEvent;
import edu.uph.learn.maharadja.game.event.TroopMovementEvent;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.player.Bot;
import edu.uph.learn.maharadja.player.Player;
import edu.uph.learn.maharadja.player.TroopMovementDecision;
import edu.uph.learn.maharadja.ui.event.CombatResultEvent;
import edu.uph.learn.maharadja.ui.state.LoadingState;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static edu.uph.learn.maharadja.game.event.SkipPhaseEvent.SkipReason.DO_NOTHING;
import static edu.uph.learn.maharadja.game.event.SkipPhaseEvent.SkipReason.NO_DEPLOYABLE_TROOP;

public class GameEngine {
  private static final Random RANDOM = new SecureRandom();
  private static final Logger LOG = LoggerFactory.getLogger(GameEngine.class);
  private final GameMap gameMap;
  private final GameState gameState;
  private Consumer<GameResult> onGameResult = gameResult -> {};

  //region Singleton
  GameEngine(GameMap gameMap) {
    this(gameMap, GameState.get());
  }

  GameEngine(GameMap gameMap, GameState gameState) {
    this.gameMap = gameMap;
    this.gameState = gameState;
    gameState.setGameMap(gameMap);
    initialDistribution();
    nextPhase();
  }

  private static class GameEngineHolder {
    private static GameEngine INSTANCE;
  }

  public static GameEngine init(GameMap gameMap) {
    GameEngineHolder.INSTANCE = new GameEngine(gameMap);
    return GameEngineHolder.INSTANCE;
  }

  public static GameEngine get() {
    return GameEngineHolder.INSTANCE;
  }
  //endregion

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

        LOG.info(
            "Player [{}] deployed [1] troop to territory [{}/{}], now has [{}] troop(s).",
            player.getUsername(), territory.getName(), territory.getRegion().getName(), territory.getNumberOfStationedTroops()
        );

        EventBus.emit(new TroopMovementEvent(player, gameState.currentPhase(), null, territory));
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

    TurnPhase nextPhase = switch (gameState.currentPhase()) {
      case START -> TurnPhase.DRAFT;
      case DRAFT -> TurnPhase.ATTACK;
      case ATTACK -> TurnPhase.FORTIFY;
      case FORTIFY -> TurnPhase.START;
    };

    gameState.setActiveTurnPhase(nextPhase);
    EventBus.emit(new GamePhaseEvent(currentPlayer, nextPhase));

    switch (nextPhase) {
      case START -> {
        checkLosingCondition(currentPlayer);
        checkWinningCondition(currentPlayer);
        nextTurn();
        nextPhase();
      }
      case DRAFT -> prepareTroopsDraft(currentPlayer);
      case ATTACK -> prepareTerritoryAttack(currentPlayer);
      case FORTIFY -> prepareTerritoryFortification(currentPlayer);
    }
  }

  @SneakyThrows
  private void prepareTroopsDraft(Player currentPlayer) {
    int numOfTroops = 0;
    // Troops based on owned territories
    numOfTroops += Math.max(3, Math.floorDiv(currentPlayer.getTerritories().size(), 3));
    // Troops based on owned region
    for (Region region : gameMap.getAllRegions()) {
      if (Objects.equals(currentPlayer, region.getOwner())) {
        numOfTroops += region.getBonusTroops();
      }
    }
    // TODO: Troops based on resource

    EventBus.emit(new DraftPhaseEvent(currentPlayer, numOfTroops));
    if (currentPlayer instanceof Bot bot) {
      Thread.sleep(2000); // simulate thinking
      draftTroop(bot.decideTroopDraft(numOfTroops));
    }
  }

  public void draftTroop(Map<Territory, Integer> draftTroopMapping) {
    for (Map.Entry<Territory, Integer> entry : draftTroopMapping.entrySet()) {
      Territory territory = entry.getKey();
      if (entry.getValue() == 0) continue;
      entry.getKey().deployTroop(entry.getValue());
      LOG.info("Player [{}] drafted [{}] troop(s) to territory [{}/{}], now has [{}] troop(s)",
          territory.getOwner().getUsername(),
          entry.getValue(),
          territory.getName(),
          territory.getRegion().getName(),
          territory.getNumberOfStationedTroops()
      );
      EventBus.emit(new TroopMovementEvent(gameState.currentTurn(), TurnPhase.DRAFT, null, entry.getKey()));
    }
    nextPhase();
  }

  @SneakyThrows
  public void prepareTerritoryAttack(Player currentPlayer) {
    List<Territory> deployableTerritories = TerritoryUtil.getDeployableTerritories(currentPlayer)
        .stream()
        .filter(territory -> {
          boolean atLeastOneAttackable = false;
          for (Territory neighbor : gameMap.getAdjacentTerritories(territory)) {
            if (gameMap.isAttackable(territory, neighbor)) {
              atLeastOneAttackable = true;
              break;
            }
          }
          return atLeastOneAttackable;
        })
        .toList();
    if (deployableTerritories.isEmpty()) {
      EventBus.emit(new SkipPhaseEvent(currentPlayer, NO_DEPLOYABLE_TROOP));
      nextPhase();
      return;
    }
    EventBus.emit(new AttackPhaseEvent(currentPlayer, deployableTerritories));
    if (currentPlayer instanceof Bot bot) {
      Thread.sleep(2000); // simulate thinking and wait for "previous combat result if any"
      while (LoadingState.get().isLoading(LoadingState.Key.COMBAT_RESULT)) {
        Thread.sleep(1000);
      }
      Optional<TroopMovementDecision> optDecision = bot.decideTerritoryAttack(deployableTerritories);
      if (optDecision.isPresent()) {
        TroopMovementDecision decision = optDecision.get();
        CombatResult result = performCombat(decision.from(), decision.to(), decision.numOfTroops());
        EventBus.emit(new CombatResultEvent(decision.from(), decision.to(), result));
        prepareTerritoryAttack(GameState.get().currentTurn());
      } else {
        LOG.info("Bot [{}] decided on ATTACK to DO NOTHING", currentPlayer.getUsername());
        EventBus.emit(new SkipPhaseEvent(currentPlayer, DO_NOTHING));
        nextPhase();
      }
    }
  }

  @SneakyThrows
  private void prepareTerritoryFortification(Player currentPlayer) {
    List<Territory> deployableTerritories = TerritoryUtil.getDeployableTerritories(currentPlayer);
    if (deployableTerritories.isEmpty()) {
      EventBus.emit(new SkipPhaseEvent(currentPlayer, NO_DEPLOYABLE_TROOP));
      nextPhase();
      return;
    }
    EventBus.emit(new FortifyPhaseEvent(currentPlayer, deployableTerritories));
    if (currentPlayer instanceof Bot bot) {
      Thread.sleep(2000); // simulate thinking
      Optional<TroopMovementDecision> optDecision = bot.decideTerritoryFortification(deployableTerritories);
      if (optDecision.isPresent()) {
        fortifyTerritory(optDecision.get().from(), optDecision.get().to(), optDecision.get().numOfTroops());
      } else {
        LOG.info("Bot [{}] decided on FORTIFY to DO NOTHING", currentPlayer.getUsername());
        EventBus.emit(new SkipPhaseEvent(currentPlayer, DO_NOTHING));
        nextPhase();
      }
    }
  }

  public void occupyTerritory(Territory territory, int playerIdx, int movedTroop) {
    occupyTerritory(territory, gameState.getPlayerList().get(playerIdx), movedTroop, null);
  }

  public void occupyTerritory(Territory source, Territory destination, int movedTroop) {
    occupyTerritory(destination, source.getOwner(), movedTroop, source);
  }

  private void occupyTerritory(Territory destination, Player player, int movedTroop, Territory source) {
    if (source != null && destination.getOwner() != null) {
      destination.getOwner().removeTerritory(destination);
    }
    destination.setOwner(player);
    destination.deployTroop(movedTroop);
    player.addTerritory(destination);

    if (source != null) {
      source.withdrawTroop(movedTroop);
      EventBus.emit(new TroopMovementEvent(player, gameState.currentPhase(), source, destination));
      EventBus.emit(new TerritoryOccupiedEvent(destination, player));
    } else {
      EventBus.emit(new TroopMovementEvent(player, gameState.currentPhase(), null, destination));
    }

    LOG.info(
        "Player [{}] occupied the territory [{}/{}] and deployed [{} troop], now has [{} troop].",
        player.getUsername(), destination.getName(), destination.getRegion().getName(), movedTroop, destination.getNumberOfStationedTroops()
    );

    // Check if occupation resulted in owning the region
    boolean regionWon = destination.getRegion()
        .getTerritories()
        .stream()
        .allMatch(territory -> Objects.equals(territory.getOwner(), player));
    if (regionWon) {
      destination.getRegion().setOwner(player);
      EventBus.emit(new RegionOccupiedEvent(destination.getRegion(), player));
    }
  }

  public List<Territory> getAdjacentTerritories(Territory origin) {
    return List.copyOf(gameMap.getAdjacentTerritories(origin));
  }

  public List<Territory> getAttackableTerritories(Territory origin) {
    return gameMap.getAdjacentTerritories(origin)
        .stream()
        .filter(territory -> !Objects.equals(origin.getOwner(), territory.getOwner()))
        .sorted(Comparator.comparingInt(Territory::getNumberOfStationedTroops).reversed())
        .collect(Collectors.toList());
  }

  public boolean isAttackable(Territory origin, Territory destination) {
    return gameMap.isAttackable(origin, destination);
  }

  public CombatResult performCombat(Territory attacker, Territory defender, int attackingTroops) {
    if (attackingTroops < 1 || attacker.getNumberOfStationedTroops()-1 < attackingTroops) {
      LOG.info("Not enough or too few attacking troops, desired {} vs actual {}", attackingTroops, attacker.getNumberOfStationedTroops());
      return null;
    }
    if (!gameMap.isAttackable(attacker, defender)) {
      LOG.info("Cannot attack {} from {}.", defender.getName(), attacker.getName());
      return null;
    }

    int defendingTroop = defender.getNumberOfStationedTroops();
    DiceRoll.RollResult rollResult = DiceRoll.playRoll(attackingTroops, defendingTroop);

    LOG.info("Attacker [{}] rolled {}.", attacker.getOwner().getUsername(), rollResult.attackerRoll());
    LOG.info("Defender [{}] rolled {}.", defender.getOwner().getUsername(), rollResult.defenderRoll());

    int attackerLost = 0;
    int defenderLost = 0;
    for (int i = 0; i < Math.min(attackingTroops, defendingTroop); i++) {
      if (rollResult.attackerRoll().get(i) > rollResult.defenderRoll().get(i)) {
        defenderLost++;
      } else {
        attackerLost++;
      }
    }

    attacker.withdrawTroop(attackerLost);
    LOG.info("Player [{}] attacked [{}] and lost [{}] troop(s).", attacker.getOwner().getUsername(), defender.getName(), attackerLost);
    defender.withdrawTroop(defenderLost);
    LOG.info("Player [{}] defended [{}] and lost [{}] troop(s).", defender.getOwner().getUsername(), defender.getName(), defenderLost);

    if (defender.getNumberOfStationedTroops() == 0) {
      Player defendingPlayer = defender.getOwner();
      occupyTerritory(attacker, defender, attackingTroops - attackerLost);
      checkLosingCondition(defendingPlayer);
      return new CombatResult(attackerLost, defenderLost, CombatResult.Result.OCCUPIED);
    }

    return new CombatResult(
        attackerLost,
        defenderLost,
        attacker.getNumberOfStationedTroops() == 1 ? CombatResult.Result.ADVANCE : CombatResult.Result.FORFEIT
    );
  }

  public List<Territory> fortifyTerritory(Territory from, Territory to, int numOfTroops) {
    List<Territory> shortestDeploymentPath = gameMap.getShortestDeploymentPath(from, to);
    if (shortestDeploymentPath.isEmpty()) {
      return shortestDeploymentPath;
    }
    from.withdrawTroop(numOfTroops);
    to.deployTroop(numOfTroops);
    EventBus.emit(new TroopMovementEvent(gameState.currentTurn(), TurnPhase.FORTIFY, from, to));
    nextPhase();
    return shortestDeploymentPath;
  }

  public List<Territory> getFortifiableTerritories(Territory origin) {
    List<Territory> fortifiableTerritories = new ArrayList<>();
    for (Territory ownedTerritory : origin.getOwner().getTerritories()) {
      if (origin.equals(ownedTerritory)) continue;
      if (!gameMap.getShortestDeploymentPath(origin, ownedTerritory).isEmpty()) {
        fortifiableTerritories.add(ownedTerritory);
      }
    }
    fortifiableTerritories.sort(Comparator.comparingInt(Territory::getNumberOfStationedTroops));
    return fortifiableTerritories;
  }

  private void checkWinningCondition(Player player) {
    // TODO impl
    if (true) {
      return;
    }
    if (GameState.get().me().equals(player)) {
      onGameResult.accept(new GameResult(player, true));
    }
  }

  private void checkLosingCondition(Player player) {
    if (player.isForfeited()) {
      return;
    }

    if (player.getTerritories().isEmpty()) {
      player.setForfeited(true);
      EventBus.emit(new PlayerForfeitEvent(player));
      if (GameState.get().me().equals(player)) {
        onGameResult.accept(new GameResult(player, false));
      }
    }
  }

  public void setOnGameResult(Consumer<GameResult> onGameResult) {
    this.onGameResult = onGameResult;
  }
}
