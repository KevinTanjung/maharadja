package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

public class BorderFactory {
  public static Border primary() {
    return color(Color.IMPERIAL_GOLD);
  }

  public static Border color(Color color) {
    return new Border(
        new BorderStroke(
            color.get(),
            BorderStrokeStyle.SOLID,
            new CornerRadii(8),
            BorderWidths.DEFAULT
        )
    );
  }
}
