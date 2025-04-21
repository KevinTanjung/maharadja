package edu.uph.learn.maharadja.ui.components;

import javafx.scene.layout.VBox;

import static edu.uph.learn.maharadja.common.UI.TAB_WIDTH;

public class SidePane extends VBox {
  public SidePane() {
    setPrefWidth(TAB_WIDTH);
    setMinWidth(TAB_WIDTH);
    setMaxWidth(TAB_WIDTH);
    getChildren().addAll(new ActionPane(), new PlayerTabPane());
  }
}
