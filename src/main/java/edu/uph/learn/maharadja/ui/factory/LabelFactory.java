package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;

public class LabelFactory {
  private LabelFactory() {
  }

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
}
