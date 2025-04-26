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
    Label label = new Label();
    label.setStyle(
        "-fx-background-color: " + Color.VOLCANIC_BLACK.toHex() + "; "
            + "-fx-background-radius: " + ((int) UI.UNIT) + "px;"
            + "-fx-border-color: " + Color.IMPERIAL_GOLD.toHex() + "; "
            + "-fx-border-radius: " + ((int) UI.UNIT) + "px;"
            + "-fx-font-weight: bold;"
            + "-fx-text-fill: " + Color.IVORY_WHITE.toHex() + ";"
            + "-fx-alignment: center;"
    );
    label.setFont(UI.SMALL_FONT);
    label.setMinWidth(UI.MEDIUM);
    label.setPrefWidth(UI.MEDIUM);
    label.setMaxWidth(UI.MEDIUM);
    label.setMinHeight(UI.MEDIUM);
    label.setPrefHeight(UI.MEDIUM);
    label.setMaxHeight(UI.MEDIUM);
    label.setLayoutX(UI.UNIT * -1);
    label.setLayoutY(UI.UNIT * -1);
    label.setMouseTransparent(true);
    label.setVisible(false);
    return label;
  }

  private LabelFactory() {
  }
}
