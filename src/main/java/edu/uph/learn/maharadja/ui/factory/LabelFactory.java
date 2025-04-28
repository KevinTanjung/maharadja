package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;

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
    double size = UI.EXTRA_LARGE;
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
        + "-fx-background-radius: " + ((int) UI.EXTRA_LARGE) + "px;"
        + "-fx-border-color: " + backgroundColor.toHex() + "; "
        + "-fx-border-radius: " + ((int) UI.EXTRA_LARGE) + "px;"
        + "-fx-font-weight: bold;"
        + "-fx-text-fill: " + textColor.toHex() + ";"
        + "-fx-alignment: center;";
  }

  @SafeVarargs
  public static TextFlow createTextFlow(Pair<String, Boolean>... texts) {
    TextFlow textFlow = new TextFlow();
    for (Pair<String, Boolean> text : texts) {
      textFlow.getChildren().add(createText(text.getKey(), text.getValue()));
    }
    return textFlow;
  }

  public static Text createText(String content, boolean bold) {
    Text text = new Text(content);
    if (bold) {
      text.setStyle("-fx-font-weight: bold;");
    }
    return text;
  }

  private LabelFactory() {
  }
}
