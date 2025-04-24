package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.game.event.GamePhaseEvent;
import edu.uph.learn.maharadja.ui.TextResource;
import edu.uph.learn.maharadja.ui.event.MapTileSelectedEvent;
import edu.uph.learn.maharadja.ui.factory.LabelFactory;
import edu.uph.learn.maharadja.ui.form.ReinforceTroopForm;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionPane extends BorderPane {
  private static final Logger LOG = LoggerFactory.getLogger(ActionPane.class);
  private final ReinforceTroopForm reinforceTroopForm;
  private MapTile sourceTile;
  private MapTile destinationTile;

  public ActionPane() {
    EventBus.registerListener(MapTileSelectedEvent.class, this::onMapTileSelected);
    EventBus.registerListener(GamePhaseEvent.class, this::onGamePhase);
    this.reinforceTroopForm = new ReinforceTroopForm();

    setHeight(UI.ACTION_PANE_HEIGHT);
    setPrefHeight(UI.ACTION_PANE_HEIGHT);
    setMinHeight(UI.ACTION_PANE_HEIGHT);
    setMaxHeight(UI.ACTION_PANE_HEIGHT);
    setPadding(new Insets(UI.SMALL));
  }

  private void onGamePhase(GamePhaseEvent gamePhaseEvent) {
    LOG.info("[onGamePhase] Player {} turn, Phase {}", gamePhaseEvent.currentPlayer().getUsername(), gamePhaseEvent.phase());
    if (gamePhaseEvent.phase() == TurnPhase.REINFORCEMENT) {
      renderReinforcementAction();
    }
  }

  private void onMapTileSelected(MapTileSelectedEvent event) {
    sourceTile = event.mapTile();
    setCenter(null);
    setTop(new Label(sourceTile.getTerritory().getName()));
    GridPane pane = new GridPane();
    setCenter(pane);

    Button button = new Button("Back");
    button.setOnMouseClicked(mouseEvent -> {
      switch (GameState.get().currentPhase()) {
        case REINFORCEMENT:
          renderReinforcementAction();
          break;
      }
    });
    setBottom(button);
  }

  private void renderReinforcementAction() {
    setTop(LabelFactory.create(
        TextResource.REINFORCEMENT_TITLE,
        Color.VOLCANIC_BLACK,
        Color.IVORY_WHITE,
        UI.TAB_WIDTH
    ));
    setCenter(reinforceTroopForm);
    setBottom(reinforceTroopForm.getSubmitButton());
    reinforceTroopForm.setVisible(true);
  }
}
