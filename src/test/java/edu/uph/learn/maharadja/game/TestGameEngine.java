package edu.uph.learn.maharadja.game;

import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.map.GameMapLoader;
import edu.uph.learn.maharadja.map.MapType;
import edu.uph.learn.maharadja.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGameEngine {
  @Test
  public void gameEngine_HasTurnManagement() {
    EventBus.init();
    GameState state = new GameState();
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(new Player("Ken Dedes"))
    );
    assertEquals(
        RegisterPlayerResult.SUCCESS,
        state.registerPlayer(new Player("Ken Arok"))
    );
    state.start();

    GameEngine engine = new GameEngine(GameMapLoader.load(MapType.CLASSIC), state);
    assertEquals(TurnPhase.DRAFT, state.currentPhase());
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.ATTACK, state.currentPhase());
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.FORTIFY, state.currentPhase());
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());

    // next user
    engine.nextPhase();
    assertEquals(TurnPhase.START, state.currentPhase());
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.DRAFT, state.currentPhase());
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.ATTACK, state.currentPhase());
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    assertFalse(state.currentTurn().isComputer());
    engine.nextPhase();
    assertEquals(TurnPhase.FORTIFY, state.currentPhase());
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
    engine.nextPhase();
    assertEquals("Computer 2", state.currentTurn().getUsername());
    assertTrue(state.currentTurn().isComputer());

    engine.nextPhase(); // START
    engine.nextPhase(); // REINFORCEMENT
    engine.nextPhase(); // ATTACK
    engine.nextPhase(); // FORTIFY
    assertEquals("Ken Dedes", state.currentTurn().getUsername());
    engine.nextPhase(); // START
    engine.nextPhase(); // REINFORCEMENT
    engine.nextPhase(); // ATTACK
    engine.nextPhase(); // FORTIFY
    assertEquals("Ken Arok", state.currentTurn().getUsername());
    engine.nextPhase(); // START
    engine.nextPhase(); // REINFORCEMENT
    engine.nextPhase(); // ATTACK
    engine.nextPhase(); // FORTIFY
    engine.nextPhase(); // START
    engine.nextPhase(); // REINFORCEMENT
    engine.nextPhase(); // ATTACK
    engine.nextPhase(); // FORTIFY
    assertEquals("Computer 2", state.currentTurn().getUsername());
  }
}
