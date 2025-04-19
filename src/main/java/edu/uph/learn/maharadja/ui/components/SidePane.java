package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.Player;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static edu.uph.learn.maharadja.common.UI.TAB_WIDTH;

public class SidePane extends VBox {

  public SidePane() {
    setBackground(Background.fill(Color.VOLCANIC_BLACK.get()));
    setPrefWidth(TAB_WIDTH);
    setMinWidth(TAB_WIDTH);
    setMaxWidth(TAB_WIDTH);


    TabPane tabPane = new TabPane();
    tabPane.setSide(Side.RIGHT);
    tabPane.setMinWidth(TAB_WIDTH);
    tabPane.setMaxWidth(TAB_WIDTH);
    tabPane.setPrefWidth(TAB_WIDTH);
    VBox.setVgrow(tabPane, Priority.ALWAYS);

    for (Player player : GameState.getPlayerList()) {
      Tab tab = new Tab(player.getUsername(), new HBox());
      tab.setClosable(false);
      tabPane.getTabs().add(tab);
    }
    getChildren().add(tabPane);
  }
}
