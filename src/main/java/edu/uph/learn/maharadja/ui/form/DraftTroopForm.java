package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.game.event.DraftPhaseEvent;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import edu.uph.learn.maharadja.ui.state.TileSelectionState;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DraftTroopForm extends BaseActionForm {
  private static final Logger LOG = LoggerFactory.getLogger(DraftTroopForm.class);

  private final GridPane gridPane;
  private final SimpleIntegerProperty draftedTroop = new SimpleIntegerProperty();
  private final Map<Territory, SimpleIntegerProperty> troopAssignment = new HashMap<>();
  private final ObservableList<Territory> sourceTerritoryOptions = TileSelectionState.get().getValidSources();
  private final Button submitButton;

  public DraftTroopForm() {
    super();
    EventBus.registerListener(DraftPhaseEvent.class, this::onDraftPhaseEvent);

    gridPane = new GridPane();
    gridPane.setVgap(UI.UNIT);
    gridPane.setHgap(UI.SMALL);
    gridPane.setPadding(new Insets(UI.SMALL));
    gridPane.setAlignment(Pos.CENTER);
    gridPane.setGridLinesVisible(true);
    gridPane.setMinWidth(this.getMinWidth());
    gridPane.setPrefWidth(this.getPrefWidth());
    gridPane.setMaxWidth(this.getMaxWidth());
    setContent(gridPane);

    submitButton = ButtonFactory.primary("DEPLOY", UI.TAB_WIDTH - UI.UNIT);
    submitButton.setOnAction(actionEvent -> {
      sourceTerritoryOptions.clear();
      GameEngine.get().draftTroop(
          troopAssignment.entrySet()
              .stream()
              .map(entry -> Map.entry(entry.getKey(), entry.getValue().get()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
      );
    });
  }

  void onDraftPhaseEvent(DraftPhaseEvent event) {
    LOG.info("[onDraftPhaseEvent] Player = {}, Number of Troops = {}", event.player().getUsername(), event.numOfTroops());
    resetForm();
    sourceTerritoryOptions.addAll(event.player().getTerritories());

    // Assignable Troop
    Label label = new Label("Deployable Troops:");
    label.setAlignment(Pos.CENTER_LEFT);
    gridPane.add(label, 0, 0, 2, 1);
    submitButton.disableProperty().bind(draftedTroop.isEqualTo(event.numOfTroops()).not());

    Label numOfTroop = new Label();
    numOfTroop.textProperty().bind(
        draftedTroop.subtract(event.numOfTroops())
            .multiply(-1)
            .asString()
    );
    numOfTroop.setFont(UI.SMALL_FONT);
    numOfTroop.setTextAlignment(TextAlignment.RIGHT);
    gridPane.add(numOfTroop, 3, 0, 4, 1);

    int idx = 0;
    for (Territory territory : event.player().getTerritories()) {
      int col = (idx % 2);
      int row = (idx / 2) + 1; // skip 1st row

      SimpleIntegerProperty currentTroopCount = new SimpleIntegerProperty(0);
      troopAssignment.put(territory, currentTroopCount);

      // Container
      VBox gridContainer = new VBox();
      HBox firstRow = new HBox();
      HBox secondRow = new HBox(UI.SMALL);
      gridContainer.getChildren().addAll(firstRow, secondRow);

      // Territory Label
      Label territoryNameLabel = new Label(territory.getName());
      territoryNameLabel.setMaxWidth(Double.MAX_VALUE);
      territoryNameLabel.setTextAlignment(TextAlignment.CENTER);
      territoryNameLabel.setContentDisplay(ContentDisplay.CENTER);
      HBox.setHgrow(territoryNameLabel, Priority.ALWAYS);
      firstRow.getChildren().add(territoryNameLabel);

      // Decrement Button
      Button decrementButton = renderButton("-");
      decrementButton.disableProperty().bind(
          currentTroopCount.lessThanOrEqualTo(0)
              .or(draftedTroop.lessThanOrEqualTo(0))
      );
      decrementButton.setOnAction(actionEvent -> {
        draftedTroop.set(draftedTroop.get() - 1);
        troopAssignment.get(territory).set(troopAssignment.get(territory).get() - 1);
      });

      // Troop Count
      Label troopCount = new Label();
      troopCount.setTextAlignment(TextAlignment.CENTER);
      troopCount.setMinWidth(Region.USE_PREF_SIZE);
      troopCount.setMaxWidth(Double.MAX_VALUE);
      troopCount.setMinHeight(UI.UNIT * 3);
      troopCount.setTextAlignment(TextAlignment.CENTER);
      troopCount.setContentDisplay(ContentDisplay.CENTER);
      troopCount.textProperty().bind(
          troopAssignment.get(territory)
              .add(territory.getNumberOfStationedTroops())
              .asString()
      );
      HBox.setHgrow(troopCount, Priority.ALWAYS);

      // Increment Button
      Button incrementButton = renderButton("+");
      incrementButton.disableProperty().bind(draftedTroop.greaterThanOrEqualTo(event.numOfTroops()));
      incrementButton.setOnAction(mouseEvent -> {
        draftedTroop.set(draftedTroop.get() + 1);
        currentTroopCount.set(currentTroopCount.get() + 1);
      });

      secondRow.getChildren().addAll(decrementButton, troopCount, incrementButton);
      gridPane.add(gridContainer, col, row);
      idx++;
    }

    setVisible(true);
  }

  private void resetForm() {
    gridPane.getChildren().clear();
    draftedTroop.set(0);
    troopAssignment.clear();
  }

  public Button getSubmitButton() {
    return submitButton;
  }
}
