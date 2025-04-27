package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.map.GameMap;

import java.util.LinkedList;
import java.util.List;

/**
 * Anything related to the game state will be visible here.
 */
public class GameState {
  private boolean started;
  private int activeTurn;
  private TurnPhase activeTurnPhase;
  private Player thisPlayer;
  private List<Player> playerList = new LinkedList<>();
  private GameMap gameMap;

  //region Singleton
  // VisibleForTesting
  GameState() {
    started = false;
    activeTurn = 0;
    activeTurnPhase = TurnPhase.START;
    // First, fill all players with Computer
    for (int i = 1; i <= Constant.MAX_PLAYERS; i++) {
      playerList.add(Player.computer(i));
    }
  }

  private static class GameStateHolder {
    private static GameState INSTANCE;
  }

  public static void init() {
    if (GameStateHolder.INSTANCE == null) {
      GameStateHolder.INSTANCE = new GameState();
    }
  }

  public static GameState get() {
    if (GameStateHolder.INSTANCE == null) {
      init();
    }
    return GameStateHolder.INSTANCE;
  }

  public static void reset() {
    GameStateHolder.INSTANCE = null;
    init();
  }
  //endregion

  public StartResult start() {
    if (started) {
      return StartResult.ALREADY_STARTED;
    }
    int nonComputerPlayer = getNonComputerPlayer();
    if (nonComputerPlayer == 0) {
      return StartResult.ONLY_BOTS;
    }

    started = true;
    playerList = List.copyOf(playerList);
    return StartResult.SUCCESS;
  }

  public Player me() {
    return get().thisPlayer;
  }

  public RegisterPlayerResult registerPlayer(Player player) {
    return registerPlayer(player, false);
  }

  public RegisterPlayerResult registerPlayer(
      final Player player,
      final boolean currentPlayer
  ) {
    if (started) {
      return RegisterPlayerResult.GAME_ALREADY_STARTED;
    }

    final int nonComputerPlayer = getNonComputerPlayer();
    if (nonComputerPlayer == Constant.MAX_PLAYERS) {
      return RegisterPlayerResult.GAME_ALREADY_FULL;
    }
    player.setColor(Player.PLAYER_COLORS.get(3 - nonComputerPlayer));
    playerList.add(nonComputerPlayer, player);
    if (currentPlayer) {
      thisPlayer = player;
    }
    playerList.remove(Constant.MAX_PLAYERS);
    return RegisterPlayerResult.SUCCESS;
  }

  public Player currentTurn() {
    return playerList.get(activeTurn);
  }

  public List<Player> getPlayerList() {
    return playerList;
  }

  public TurnPhase currentPhase() {
    return activeTurnPhase;
  }

  public GameMap getGameMap() {
    return gameMap;
  }

  void setGameMap(GameMap gameMap) {
    this.gameMap = gameMap;
  }

  void setActiveTurn(int activeTurn) {
    this.activeTurn = activeTurn;
  }

  void setActiveTurnPhase(TurnPhase activeTurnPhase) {
    this.activeTurnPhase = activeTurnPhase;
  }

  private int getNonComputerPlayer() {
    int nonComputerPlayer = 0;
    for (Player currentPlayer : playerList) {
      if (!currentPlayer.isComputer()) {
        nonComputerPlayer++;
      }
    }
    return nonComputerPlayer;
  }
}
