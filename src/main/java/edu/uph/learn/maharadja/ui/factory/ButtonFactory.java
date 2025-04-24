package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.Color;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;

import static edu.uph.learn.maharadja.common.Constant.HEIGHT_NORMAL;
import static edu.uph.learn.maharadja.common.UI.SMALL_FONT;

public class ButtonFactory {
  public static Button primary(String label, double width) {
    Button button = new Button(label);
    button.setFont(SMALL_FONT);
    button.setMinHeight(HEIGHT_NORMAL);
    button.setPrefHeight(HEIGHT_NORMAL);
    button.setMinWidth(width);
    button.setPrefWidth(width);
    button.setBackground(Background.fill(Color.IMPERIAL_GOLD.get()));
    button.setBorder(BorderFactory.primary());
    return button;
  }
}
