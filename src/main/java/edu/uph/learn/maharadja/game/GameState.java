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
  private Player thisPlayer;
  private final List<Player> playerList = new LinkedList<>();
  private GameMap gameMap;

  //region Singleton
  // VisibleForTesting
  GameState() {
    // First, fill all players with Computer
    for (int i = 1; i <= Constant.MAX_PLAYERS; i++) {
      playerList.add(Player.computer(i));
    }
  }

  private static class GameStateHolder {
    private static GameState INSTANCE;
  }

  public static GameState init() {
    GameStateHolder.INSTANCE = new GameState();
    return GameStateHolder.INSTANCE;
  }

  public static GameState get() {
    return GameStateHolder.INSTANCE;
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
    return StartResult.SUCCESS;
  }

  public static Player me() {
    return get().thisPlayer;
  }

  public static List<Player> getPlayerList() {
    return get().playerList;
  }

  public static GameMap getGameMap() {
    return get().gameMap;
  }

  public static void setGameMap(GameMap gameMap) {
    GameStateHolder.INSTANCE.gameMap = gameMap;
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

  public void nextTurn() {
    if (activeTurn == playerList.size() - 1) {
      activeTurn = 0;
    } else {
      activeTurn++;
    }
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
