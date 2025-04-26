package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;

public class LabelFactory {
  public static Label create(String content,
                             Color background,
                             Color text,
                             double width) {
    Label label = new Label(content);
    label.setStyle("-fx-text-fill: " + text.toHex());
    label.setFont(UI.LARGE_FONT);
    label.setPadding(new Insets(UI.UNIT, UI.SMALL, UI.UNIT, UI.SMALL));
    label.setMinWidth(width);
    label.setAlignment(Pos.CENTER);
    label.setBackground(Background.fill(background.get()));
    return label;
  }

  public static Label troopLabel() {
    double size = UI.LARGE;
    Label label = new Label();
    label.setStyle(troopLabelStyle(Color.VOLCANIC_BLACK, Color.IVORY_WHITE));
    label.setFont(UI.SMALL_FONT);
    label.setMinWidth(size);
    label.setPrefWidth(size);
    label.setMaxWidth(size);
    label.setMinHeight(size);
    label.setPrefHeight(size);
    label.setMaxHeight(size);
    label.setLayoutX(size * -1 / 2);
    label.setLayoutY(size * -1 / 2);
    label.setMouseTransparent(true);
    return label;
  }

  public static String troopLabelStyle(Color backgroundColor, Color textColor) {
    return "-fx-background-color: " + backgroundColor.toHex() + "; "
        + "-fx-background-radius: " + ((int) UI.LARGE) + "px;"
        + "-fx-border-color: " + backgroundColor.toHex() + "; "
        + "-fx-border-radius: " + ((int) UI.LARGE) + "px;"
        + "-fx-font-weight: bold;"
        + "-fx-text-fill: " + textColor.toHex() + ";"
        + "-fx-alignment: center;";
  }

  private LabelFactory() {
  }
}
