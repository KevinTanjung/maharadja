package edu.uph.learn.maharadja.event;

import edu.uph.learn.maharadja.game.CombatResult;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.player.Player;

import java.util.Optional;

public record BotActionEvent(TurnPhase currentPhase,
                             Player currentPlayer,
                             Optional<CombatResult> combatResult,
                             Optional<Territory> from,
                             Optional<Territory> to,
                             Runnable callback) implements Event {
  public BotActionEvent(TurnPhase currentPhase,
                        Player currentPlayer,
                        Runnable callback) {
    this(currentPhase, currentPlayer, Optional.empty(), Optional.empty(), Optional.empty(), callback);
  }
}
