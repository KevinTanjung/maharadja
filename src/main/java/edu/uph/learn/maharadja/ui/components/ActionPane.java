package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.game.event.GamePhaseEvent;
import edu.uph.learn.maharadja.ui.TextResource;
import edu.uph.learn.maharadja.ui.event.TerritorySelectedEvent;
import edu.uph.learn.maharadja.ui.factory.LabelFactory;
import edu.uph.learn.maharadja.ui.form.AttackTerritoryForm;
import edu.uph.learn.maharadja.ui.form.DraftTroopForm;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionPane extends BorderPane {
  private static final Logger LOG = LoggerFactory.getLogger(ActionPane.class);

  private final DraftTroopForm draftTroopForm;
  private final AttackTerritoryForm attackTerritoryForm;

  //private final ObjectProperty<MapTile> sourceTile = new SimpleObjectProperty<>();
  //private final ObjectProperty<MapTile> destinationTile = new SimpleObjectProperty<>();

  public ActionPane() {
    EventBus.registerListener(TerritorySelectedEvent.class, this::onTerritorySelectedEvent);
    EventBus.registerListener(GamePhaseEvent.class, this::onGamePhase);
    this.draftTroopForm = new DraftTroopForm();
    this.attackTerritoryForm = new AttackTerritoryForm();

    setBackground(Background.fill(Color.IVORY_WHITE.get()));
    setHeight(UI.ACTION_PANE_HEIGHT);
    setPrefHeight(UI.ACTION_PANE_HEIGHT);
    setMinHeight(UI.ACTION_PANE_HEIGHT);
    setMaxHeight(UI.ACTION_PANE_HEIGHT);
    setPadding(new Insets(UI.SMALL));
  }

  private void onGamePhase(GamePhaseEvent gamePhaseEvent) {
    LOG.info("[onGamePhase] Player {} turn, Phase {}", gamePhaseEvent.currentPlayer().getUsername(), gamePhaseEvent.phase());
    if (gamePhaseEvent.phase() == TurnPhase.DRAFT) {
      renderDraftAction();
    } else if (gamePhaseEvent.phase() == TurnPhase.ATTACK) {
      renderAttackAction();
    }
  }

  @SneakyThrows
  private void resetPane() {
    setTop(new Label("Loading..."));
    setCenter(null);
    setBottom(null);
    Thread.sleep(250);
  }

  private void onTerritorySelectedEvent(TerritorySelectedEvent event) {
    //if (event.eventSource() instanceof ActionPane) return;

    //if (GameState.get().currentPhase() == TurnPhase.ATTACK) {
    //  sourceTile.set(event.mapTile());
    //  return;
    //}
    //sourceTile = event.mapTile();
    //setCenter(null);
    //setTop(new Label(sourceTile.getTerritory().getName()));
    //GridPane pane = new GridPane();
    //setCenter(pane);
    //
    //Button button = new Button("Back");
    //button.setOnMouseClicked(mouseEvent -> {
    //  switch (GameState.get().currentPhase()) {
    //    case DRAFT:
    //      renderDraftAction();
    //      break;
    //    case ATTACK:
    //      renderAttackAction();
    //      break;
    //  }
    //});
    //setBottom(button);
  }

  private void renderDraftAction() {
    setTop(LabelFactory.create(
        TextResource.DRAFT_TITLE,
        Color.VOLCANIC_BLACK,
        Color.IVORY_WHITE,
        UI.TAB_WIDTH - UI.UNIT
    ));
    setCenter(draftTroopForm);
    setBottom(draftTroopForm.getSubmitButton());
    draftTroopForm.setVisible(true);
  }

  private void renderAttackAction() {
    setTop(LabelFactory.create(
        TextResource.ATTACK_TITLE,
        Color.SUNSET_RED,
        Color.IVORY_WHITE,
        UI.TAB_WIDTH - UI.UNIT
    ));
    setCenter(attackTerritoryForm);
    HBox hbox = new HBox(UI.UNIT);
    HBox.setHgrow(attackTerritoryForm.getSubmitButton(), Priority.ALWAYS);
    HBox.setHgrow(attackTerritoryForm.getEndButton(), Priority.ALWAYS);
    hbox.getChildren().addAll(attackTerritoryForm.getSubmitButton(), attackTerritoryForm.getEndButton());
    setBottom(hbox);
  }
}
