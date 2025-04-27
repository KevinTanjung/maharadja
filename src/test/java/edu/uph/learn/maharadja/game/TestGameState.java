package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGameState {
  @Test
  public void gameState_IsInitializedWithComputer_AndCannotBeStartedWithAllBot() {
    GameState state = new GameState();
    assertEquals(StartResult.ONLY_BOTS, state.start());
    state.registerPlayer(new Player("Ken Dedes"));
    assertEquals(StartResult.SUCCESS, state.start());
    assertEquals(StartResult.ALREADY_STARTED, state.start());
  }

  @Test
  public void gameState_CannotRegisterPlayer_WhenAlreadyStarted() {
    GameState state = new GameState();
    assertEquals(RegisterPlayerResult.SUCCESS, state.registerPlayer(new Player("Ken Dedes")));
    state.start();
    assertEquals(RegisterPlayerResult.GAME_ALREADY_STARTED, state.registerPlayer(new Player("Ken Arok")));
  }

  @Test
  public void gameState_CanOnlyHave4Players() {
    GameState state = new GameState();

    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(new Player("Ken Dedes"))
    );
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(new Player("Ken Arok"))
    );
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(new Player("Gajah Mada"))
    );
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(new Player("Hayam Wuruk"))
    );
    assertEquals(
        RegisterPlayerResult.GAME_ALREADY_FULL,
        state.registerPlayer(new Player("Raden Wijaya"))
    );
  }
}
