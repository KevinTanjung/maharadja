package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.Color;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;

import static edu.uph.learn.maharadja.common.Constant.HEIGHT_NORMAL;
import static edu.uph.learn.maharadja.common.UI.SMALL_FONT;

public class ButtonFactory {
  private ButtonFactory() {
  }

  public static Button create(String label,
                              double width,
                              Color text,
                              Color background) {
    Button button = new Button(label);
    button.setStyle("-fx-text-fill: " + text.toHex());
    button.setFont(SMALL_FONT);
    button.setMinHeight(HEIGHT_NORMAL);
    button.setPrefHeight(HEIGHT_NORMAL);
    button.setMinWidth(width);
    button.setPrefWidth(width);
    button.setBackground(Background.fill(background.get()));
    button.setBorder(BorderFactory.color(background));
    return button;
  }

  public static Button primary(String label, double width) {
    return create(label, width, Color.VOLCANIC_BLACK, Color.IMPERIAL_GOLD);
  }

  public static Button square(String label,
                              double width,
                              Color text,
                              Color background) {
    Button button = new Button(label);
    button.setStyle("-fx-text-fill: " + text.toHex());
    button.setFont(SMALL_FONT);
    button.setMinHeight(width);
    button.setPrefHeight(width);
    button.setMaxHeight(width);
    button.setMinWidth(width);
    button.setPrefHeight(width);
    button.setMaxHeight(width);
    button.setBackground(Background.fill(background.get()));
    button.setBorder(BorderFactory.color(background));
    return button;
  }
}
