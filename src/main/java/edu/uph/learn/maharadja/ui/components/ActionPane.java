package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.game.event.GamePhaseEvent;
import edu.uph.learn.maharadja.ui.TextResource;
import edu.uph.learn.maharadja.ui.factory.LabelFactory;
import edu.uph.learn.maharadja.ui.form.AttackTerritoryForm;
import edu.uph.learn.maharadja.ui.form.DraftTroopForm;
import edu.uph.learn.maharadja.ui.form.EndableActionForm;
import edu.uph.learn.maharadja.ui.form.FortifyTerritoryForm;
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
  private final FortifyTerritoryForm fortifyTerritoryForm;

  public ActionPane() {
    EventBus.registerListener(GamePhaseEvent.class, this::onGamePhase);
    this.draftTroopForm = new DraftTroopForm();
    this.attackTerritoryForm = new AttackTerritoryForm();
    this.fortifyTerritoryForm = new FortifyTerritoryForm();

    setBackground(Background.fill(Color.IVORY_WHITE.get()));
    setHeight(UI.ACTION_PANE_HEIGHT);
    setPrefHeight(UI.ACTION_PANE_HEIGHT);
    setMinHeight(UI.ACTION_PANE_HEIGHT);
    setMaxHeight(UI.ACTION_PANE_HEIGHT);
    setPadding(new Insets(UI.SMALL));
  }

  private void onGamePhase(GamePhaseEvent gamePhaseEvent) {
    LOG.info("[onGamePhase] Player {} turn, Phase {}", gamePhaseEvent.currentPlayer().getUsername(), gamePhaseEvent.phase());
    resetPane();
    draftTroopForm.setVisible(false);
    attackTerritoryForm.setVisible(false);
    fortifyTerritoryForm.setVisible(false);

    if (gamePhaseEvent.phase() == TurnPhase.DRAFT) {
      renderDraftAction();
    } else if (gamePhaseEvent.phase() == TurnPhase.ATTACK) {
      renderAttackAction();
    } else if (gamePhaseEvent.phase() == TurnPhase.FORTIFY) {
      renderFortifyAction();
    }
  }

  @SneakyThrows
  private void resetPane() {
    setTop(new Label("Loading..."));
    setCenter(null);
    setBottom(null);
    Thread.sleep(250);
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
    renderEndableActionForm(attackTerritoryForm);
  }

  private void renderFortifyAction() {
    setTop(LabelFactory.create(
        TextResource.FORTIFY_TITLE,
        Color.SKY_BLUE,
        Color.VOLCANIC_BLACK,
        UI.TAB_WIDTH - UI.UNIT
    ));
    renderEndableActionForm(fortifyTerritoryForm);
  }

  private void renderEndableActionForm(EndableActionForm form) {
    setCenter(form);
    HBox hbox = new HBox(UI.UNIT);
    HBox.setHgrow(form.getSubmitButton(), Priority.ALWAYS);
    HBox.setHgrow(form.getEndButton(), Priority.ALWAYS);
    hbox.getChildren().addAll(form.getSubmitButton(), form.getEndButton());
    setBottom(hbox);
    form.setVisible(true);
  }
}
