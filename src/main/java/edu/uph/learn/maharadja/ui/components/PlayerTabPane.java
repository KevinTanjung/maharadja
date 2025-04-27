package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.game.event.GamePhaseEvent;
import edu.uph.learn.maharadja.player.Player;
import javafx.geometry.Side;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class PlayerTabPane extends TabPane {
  private final Map<Player, PlayerTab> playerTabMapping = new HashMap<>();

  public PlayerTabPane() {
    EventBus.registerListener(GamePhaseEvent.class, this::onGamePhaseEvent);

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
      PlayerTab playerTab = new PlayerTab(player);
      playerTabMapping.put(player, playerTab);
      getTabs().add(playerTab);
    }

    getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      for (Map.Entry<Player, PlayerTab> entry : playerTabMapping.entrySet()) {
        if (newVal == entry.getValue()) {
          entry.getValue().renderTabTitle(entry.getKey());
        } else {
          entry.getValue().renderTabTitle(null);
        }
      }
    });
  }

  private void onGamePhaseEvent(GamePhaseEvent gamePhaseEvent) {
    if (gamePhaseEvent.phase() == TurnPhase.DRAFT) {
      PlayerTab currentTurnTab = playerTabMapping.get(gamePhaseEvent.currentPlayer());
      getSelectionModel().select(currentTurnTab);
    }
  }
}
