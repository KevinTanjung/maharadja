package edu.uph.learn.maharadja.ui.state;

import javafx.scene.Node;

import java.util.List;
import java.util.Optional;

public record DialogTask(String title,
                         Optional<String> message,
                         Optional<List<Node>> contents,
                         Runnable callback,
                         Optional<Integer> autoDismissSeconds) {
  public DialogTask(String title) {
    this(title, Optional.empty(), Optional.empty(), () -> {}, Optional.of(3));
  }

  public DialogTask(String title,
                    String message,
                    Runnable callback) {
    this(title, Optional.of(message), Optional.empty(), callback, Optional.of(2));
  }

  public DialogTask(String title, List<Node> contents, Runnable callback) {
    this(title, Optional.empty(), Optional.of(contents), callback, Optional.of(2));
  }
}
