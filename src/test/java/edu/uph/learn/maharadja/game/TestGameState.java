package edu.uph.learn.maharadja.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  public void gameState_HasTurnManagement() {
    GameState state = new GameState();

    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(Player.user("Ken Dedes"))
    );
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(Player.user("Ken Arok"))
    );

    state.start();
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    state.nextTurn();
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    state.nextTurn();
    assertEquals("Computer 1", state.currentTurn().getUsername());
    assertTrue(state.currentTurn().isComputer());
    state.nextTurn();
    assertEquals("Computer 2", state.currentTurn().getUsername());
    assertTrue(state.currentTurn().isComputer());
    state.nextTurn();
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    state.nextTurn();
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    state.nextTurn();
    state.nextTurn();
    assertEquals("Computer 2", state.currentTurn().getUsername());
  }
}
