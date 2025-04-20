package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.ui.event.MapTileSelectedEvent;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static edu.uph.learn.maharadja.common.UI.TAB_WIDTH;

public class SidePane extends VBox {
  private static final Logger log = LoggerFactory.getLogger(SidePane.class);

  public SidePane() {
    EventBus.registerListener(MapTileSelectedEvent.class, this::onMapTileSelected);

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
      Tab tab = new SidePlayerTab(player);
      tabPane.getTabs().add(tab);
    }
    getChildren().add(tabPane);
  }

  private void onMapTileSelected(MapTileSelectedEvent event) {
    MapTile mapTile = event.mapTile();
    log.info("MapTile {} selected: {} {}", mapTile.getTileType(), mapTile.getTerritory().getName(), mapTile.getRegion().getName());
  }
}
