package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;

public abstract class BaseActionForm extends ScrollPane {
  public BaseActionForm() {
    super();
    setBackground(Background.fill(Color.IVORY_WHITE.get()));
    setFitToWidth(true);
    setFitToHeight(true);
    setHbarPolicy(ScrollBarPolicy.NEVER);
  }

  protected Button renderButton(String label) {
    return ButtonFactory.square(label, UI.EXTRA_LARGE, Color.IVORY_WHITE, Color.VOLCANIC_BLACK);
  }
}
