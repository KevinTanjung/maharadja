package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.Player;
import javafx.geometry.Side;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PlayerTabPane extends TabPane {
  public PlayerTabPane() {
    // tab title position
    setSide(Side.RIGHT);
    setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

    // tab pane size
    setMinWidth(UI.TAB_WIDTH);
    setMaxWidth(UI.TAB_WIDTH);
    setPrefWidth(UI.TAB_WIDTH);
    VBox.setVgrow(this, Priority.ALWAYS);

    // player tabs
    for (Player player : GameState.get().getPlayerList()) {
      getTabs().add(new PlayerTab(player));
    }
  }
}
