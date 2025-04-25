package edu.uph.learn.maharadja.utils;

import javafx.scene.Node;

public class UIUtil {
  public static void debug(Node... nodes) {
    for (Node n : nodes) {
      n.setStyle("-fx-border-color: red; -fx-border-width: 1");
    }
  }

  private UIUtil() {
  }
}
