package edu.uph.learn.maharadja.utils;

import edu.uph.learn.maharadja.common.Color;
import javafx.scene.Node;

public class UIUtil {
  public static void debug(Node... nodes) {
    for (Node n : nodes) {
      n.setStyle("-fx-border-color: red; -fx-border-width: 1");
    }
  }

  public static javafx.scene.paint.Color alpha(Color color, double alpha) {
    return javafx.scene.paint.Color.web(color.toHex(), alpha);
  }

  private UIUtil() {
  }
}
