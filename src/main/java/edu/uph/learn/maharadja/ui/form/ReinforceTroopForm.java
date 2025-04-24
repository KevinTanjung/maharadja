package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.game.event.ReinforcementPhaseEvent;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ReinforceTroopForm extends ScrollPane {
  private static final Logger LOG = LoggerFactory.getLogger(ReinforceTroopForm.class);

  private final GridPane gridPane;
  private final SimpleIntegerProperty reinforcementPhaseCounter = new SimpleIntegerProperty();
  private final Map<Territory, SimpleIntegerProperty> troopAssignment = new HashMap<>();
  private final Button submitButton;

  public ReinforceTroopForm() {
    super();
    EventBus.registerListener(ReinforcementPhaseEvent.class, this::onReinforcementPhaseEvent);

    gridPane = new GridPane();
    gridPane.setVgap(UI.UNIT);
    gridPane.setHgap(UI.SMALL);
    gridPane.setPadding(new Insets(UI.SMALL));
    gridPane.setAlignment(Pos.CENTER);
    gridPane.setGridLinesVisible(true);
    setContent(gridPane);

    setFitToWidth(true);
    setFitToHeight(true);
    setHbarPolicy(ScrollBarPolicy.NEVER);

    submitButton = ButtonFactory.primary("DEPLOY", UI.TAB_WIDTH);
    submitButton.setOnAction(actionEvent -> {
      GameEngine.get().reinforceTroop(
          troopAssignment.entrySet()
              .stream()
              .map(entry -> Map.entry(entry.getKey(), entry.getValue().get()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
      );
    });
  }

  void onReinforcementPhaseEvent(ReinforcementPhaseEvent event) {
    LOG.info("[onReinforcementPhaseEvent] Player = {}, Number of Troops = {}", event.player().getUsername(), event.numOfTroops());
    resetForm();

    // Assignable Troop
    Label label = new Label("Number of Troops:");
    label.setAlignment(Pos.CENTER_LEFT);
    gridPane.add(label, 0, 0, 2, 1);

    Label numOfTroop = new Label();
    numOfTroop.textProperty().bind(
        reinforcementPhaseCounter.subtract(event.numOfTroops())
            .multiply(-1)
            .asString()
    );
    numOfTroop.setFont(UI.SMALL_FONT);
    numOfTroop.setTextAlignment(TextAlignment.RIGHT);
    gridPane.add(numOfTroop, 1, 0);

    int i = 0;
    for (Territory territory : event.player().getTerritories()) {
      troopAssignment.put(territory, new SimpleIntegerProperty(0));

      // Territory Label
      gridPane.add(new Text(territory.getName()), 0, i*2+1, 3, 1);

      // Decrement Button
      Button decrementButton = new Button("-");
      decrementButton.disableProperty().bind(reinforcementPhaseCounter.isEqualTo(0));
      decrementButton.setOnAction(actionEvent -> {
        LOG.info("");
        if (reinforcementPhaseCounter.get() == 0) {
          return;
        }
        reinforcementPhaseCounter.subtract(1);
        troopAssignment.get(territory).subtract(1);
      });
      gridPane.add(decrementButton, 0, i*2+2);

      // Troop Count
      Label troopCount = new Label();
      troopCount.setTextAlignment(TextAlignment.CENTER);
      troopCount.textProperty().bind(
          troopAssignment.get(territory)
              .add(territory.getNumberOfStationedTroops())
              .asString()
      );
      gridPane.add(troopCount, 1, i*2+2);

      // Increment Button
      Button incrementButton = new Button("+");
      incrementButton.disableProperty().bind(reinforcementPhaseCounter.isEqualTo(event.numOfTroops()));
      incrementButton.setOnAction(mouseEvent -> {
        if (reinforcementPhaseCounter.get() == event.numOfTroops()) {
          return;
        }
        reinforcementPhaseCounter.add(1);
        troopAssignment.get(territory).add(1);
      });
      gridPane.add(incrementButton, 2, i*2+2);

      i++;
    }

    setVisible(true);
  }

  private void resetForm() {
    gridPane.getChildren().clear();
    reinforcementPhaseCounter.set(0);
    troopAssignment.clear();
  }

  public Button getSubmitButton() {
    return submitButton;
  }
}
