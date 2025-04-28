package edu.uph.learn.maharadja.ui.state;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.ui.event.CombatResultEvent;

import java.util.Optional;

public record DialogTask(String title,
                         Optional<Object> data,
                         Runnable callback,
                         Optional<Integer> autoDismissSeconds,
                         Optional<Color> backgroundColor,
                         Optional<Color> textColor) {
  public DialogTask(String title) {
    this(title, null, (Color) null);
  }

  public DialogTask(String title, Color backgroundColor, Color textColor) {
    this(title, Optional.empty(), () -> {}, Optional.of(3), Optional.ofNullable(backgroundColor), Optional.ofNullable(textColor));
  }

  public DialogTask(String title,
                    String message,
                    Runnable callback) {
    this(title, Optional.of(message), callback, Optional.of(2), Optional.empty(), Optional.empty());
  }

  public DialogTask(CombatResultEvent combatResultEvent, Runnable callback) {
    this("Combat Result", Optional.of(combatResultEvent), callback, Optional.empty(), Optional.empty(), Optional.empty());
  }

  public DialogTask(CombatResultEvent combatResultEvent, Runnable callback, int autoDismissSeconds) {
    this("Combat Result", Optional.of(combatResultEvent), callback, Optional.empty(), Optional.empty(), Optional.empty());
  }
}
