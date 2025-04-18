package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.common.Constant;

import java.util.LinkedList;
import java.util.List;

/**
 * Anything related to the game state will be visible here.
 */
public class GameState {
  private boolean started;
  private int activeTurn;
  private final List<Player> playerList = new LinkedList<>();

  //region Singleton
  // VisibleForTesting
  GameState() {
    // First, fill all players with Computer
    for (int i = 1; i <= Constant.MAX_PLAYERS; i++) {
      playerList.add(Player.computer(i));
    }
  }

  private static class GameStateHolder {
    private static final GameState INSTANCE = new GameState();
  }

  public static GameState init() {
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

  public RegisterPlayerResult registerPlayer(
      final Player player
  ) {
    if (started) {
      return RegisterPlayerResult.GAME_ALREADY_STARTED;
    }

    final int nonComputerPlayer = getNonComputerPlayer();
    if (nonComputerPlayer == Constant.MAX_PLAYERS) {
      return RegisterPlayerResult.GAME_ALREADY_FULL;
    }

    playerList.add(nonComputerPlayer, player);
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
