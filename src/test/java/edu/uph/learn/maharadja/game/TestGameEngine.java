package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGameEngine {
  @Test
  public void gameEngine_HasTurnManagement() {
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

    GameEngine engine = new GameEngine(new GameMap(), state);
    assertEquals(TurnPhase.REINFORCEMENT, state.getActiveTurnPhase());
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.ATTACK, state.getActiveTurnPhase());
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.FORTIFY, state.getActiveTurnPhase());
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());

    // next user
    engine.nextPhase();
    assertEquals(TurnPhase.REINFORCEMENT, state.getActiveTurnPhase());
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.ATTACK, state.getActiveTurnPhase());
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.FORTIFY, state.getActiveTurnPhase());
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());

    // next user
    engine.nextPhase();
    engine.nextPhase();
    assertEquals("Computer 1", state.currentTurn().getUsername());
    assertTrue(state.currentTurn().isComputer());

    engine.nextPhase();
    engine.nextPhase();
    engine.nextPhase();
    assertEquals("Computer 2", state.currentTurn().getUsername());
    assertTrue(state.currentTurn().isComputer());

    engine.nextPhase();
    engine.nextPhase();
    engine.nextPhase();
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    engine.nextPhase();
    engine.nextPhase();
    engine.nextPhase();
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    engine.nextPhase();
    engine.nextPhase();
    engine.nextPhase();
    engine.nextPhase();
    engine.nextPhase();
    engine.nextPhase();
    assertEquals("Computer 2", state.currentTurn().getUsername());
  }
}
