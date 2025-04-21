package edu.uph.learn.maharadja.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGameState {
  @Test
  public void gameState_IsInitializedWithComputer_AndCannotBeStartedWithAllBot() {
    GameState state = new GameState();
    assertEquals(StartResult.ONLY_BOTS, state.start());
    state.registerPlayer(Player.user("Ken Dedes"));
    assertEquals(StartResult.SUCCESS, state.start());
    assertEquals(StartResult.ALREADY_STARTED, state.start());
  }

  @Test
  public void gameState_CannotRegisterPlayer_WhenAlreadyStarted() {
    GameState state = new GameState();
    assertEquals(RegisterPlayerResult.SUCCESS, state.registerPlayer(Player.user("Ken Dedes")));
    state.start();
    assertEquals(RegisterPlayerResult.GAME_ALREADY_STARTED, state.registerPlayer(Player.user("Ken Arok")));
  }

  @Test
  public void gameState_CanOnlyHave4Players() {
    GameState state = new GameState();

    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(Player.user("Ken Dedes"))
    );
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(Player.user("Ken Arok"))
    );
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(Player.user("Gajah Mada"))
    );
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(Player.user("Hayam Wuruk"))
    );
    assertEquals(
        RegisterPlayerResult.GAME_ALREADY_FULL,
        state.registerPlayer(Player.user("Raden Wijaya"))
    );
  }
}
