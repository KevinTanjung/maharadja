package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.common.Color;
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
}
