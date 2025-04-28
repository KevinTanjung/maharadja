package edu.uph.learn.maharadja.common;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class UIUtil {
  public static <T extends Node> T debug(T node) {
    return debug(node, "red");
  }

  public static <T extends Node> T debug(T node, String color) {
    node.setStyle("-fx-border-color: " + color + "; -fx-border-width: 1px;");
    return node;
  }

  public static void debug(Node... nodes) {
    for (Node n : nodes) {
      n.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
    }
  }

  public static Pane debug(ImageView imageView) {
    Pane imageWrapper = new Pane(imageView);
    imageWrapper.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    return imageWrapper;
  }

  public static javafx.scene.paint.Color alpha(Color color, double alpha) {
    return javafx.scene.paint.Color.web(color.toHex(), alpha);
  }

  private UIUtil() {
  }
}
