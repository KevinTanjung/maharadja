package edu.uph.learn.maharadja.ui.factory;

import edu.uph.learn.maharadja.common.Color;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

public class BorderFactory {
  public static Border primary() {
    return new Border(
        new BorderStroke(
            Color.IMPERIAL_GOLD.get(),
            BorderStrokeStyle.SOLID,
            new CornerRadii(8),
            BorderWidths.DEFAULT
        )
    );
  }
}
