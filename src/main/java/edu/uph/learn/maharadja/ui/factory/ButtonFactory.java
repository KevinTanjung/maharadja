package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;

import static edu.uph.learn.maharadja.common.UI.LARGE_FONT;
import static edu.uph.learn.maharadja.common.UI.SMALL_FONT;

public class ButtonFactory {
  public static Button primary(String label, double width) {
    return create(label, width, Color.VOLCANIC_BLACK, Color.IMPERIAL_GOLD);
  }

  public static Button create(String label,
                              double width,
                              Color text,
                              Color background) {
    Button button = new Button(label);
    button.setStyle("-fx-text-fill: " + text.toHex());
    button.setFont(SMALL_FONT);
    button.setMinHeight(UI.EXTRA_LARGE);
    button.setPrefHeight(UI.EXTRA_LARGE);
    button.setMinWidth(width);
    button.setPrefWidth(width);
    button.setBackground(Background.fill(background.get()));
    addHoverEffect(button);
    return button;
  }

  public static Button square(String label,
                              double width,
                              Color text,
                              Color background) {
    Button button = new Button(label);
    button.setStyle("-fx-text-fill: " + text.toHex());
    button.setFont(LARGE_FONT);
    button.setMinHeight(width);
    button.setPrefHeight(width);
    button.setMaxHeight(width);
    button.setMinWidth(width);
    button.setPrefHeight(width);
    button.setMaxHeight(width);
    button.setBackground(Background.fill(background.get()));
    addHoverEffect(button);
    return button;
  }

  private static void addHoverEffect(Button button) {
    javafx.scene.paint.Color color = (javafx.scene.paint.Color) button.getBackground()
        .getFills()
        .getFirst()
        .getFill();
    button.setOnMouseEntered(e -> {
      if (!button.isDisabled()) {
        button.setCursor(Cursor.HAND);
        button.setBackground(Background.fill(color.darker()));
      }
    });
    button.setOnMouseExited(e -> {
      button.setCursor(Cursor.DEFAULT);
      button.setBackground(Background.fill(color));
    });
  }

  private ButtonFactory() {
  }
}
