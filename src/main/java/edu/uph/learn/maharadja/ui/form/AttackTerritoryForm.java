package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.CombatResult;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.game.event.AttackPhaseEvent;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.TextResource;
import edu.uph.learn.maharadja.ui.event.CombatResultEvent;
import edu.uph.learn.maharadja.ui.state.TileSelectionState;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Comparator;

public class AttackTerritoryForm extends EndableActionForm {
  private final ObservableList<Territory> sourceTerritoryOptions = TileSelectionState.get().getValidSources();
  private final ObservableList<Territory> targetTerritoryOptions = TileSelectionState.get().getValidTargets();
  private final ObjectProperty<Territory> sourceTerritory = TileSelectionState.get().selectedSourceProperty();
  private final ObjectProperty<Territory> targetTerritory = TileSelectionState.get().selectedTargetProperty();
  private final SimpleIntegerProperty numOfTroops = new SimpleIntegerProperty();

  public AttackTerritoryForm() {
    super();
    EventBus.registerListener(AttackPhaseEvent.class, this::onAttackPhaseEvent);

    VBox vBox = new VBox(UI.SMALL);
    vBox.setMaxWidth(UI.TAB_WIDTH - UI.UNIT);
    vBox.setBackground(Background.fill(Color.IVORY_WHITE.get()));
    setContent(vBox);

    // Source Territory selection
    Label sourceTerritoryLabel = new Label(TextResource.ATTACK_SOURCE_TERRITORY_LABEL);
    ChoiceBox<Territory> sourceTerritoryChoiceBox = new ChoiceBox<>();
    sourceTerritoryChoiceBox.setMaxWidth(Double.MAX_VALUE);
    sourceTerritoryChoiceBox.setItems(sourceTerritoryOptions);
    sourceTerritoryChoiceBox.valueProperty().bindBidirectional(sourceTerritory);
    sourceTerritoryChoiceBox.setConverter(new TerritoryStringConverter());
    sourceTerritory.addListener((obs, oldVal, newVal) -> {
      if (GameState.get().currentPhase() != TurnPhase.ATTACK) return;
      targetTerritory.set(null);
      if (newVal == null) {
        numOfTroops.set(0);
        targetTerritoryOptions.clear();
      } else {
        numOfTroops.setValue(newVal.getNumberOfStationedTroops() - 1);
        targetTerritoryOptions.setAll(
            GameEngine.get()
                .getAttackableTerritories(newVal)
                .toArray(Territory[]::new)
        );
      }
    });
    HBox.setHgrow(sourceTerritoryChoiceBox, Priority.ALWAYS);
    vBox.getChildren().addAll(sourceTerritoryLabel, sourceTerritoryChoiceBox);

    // Target Territory selection
    Label targetTerritoryLabel = new Label(TextResource.ATTACK_TARGET_TERRITORY_LABEL);
    ChoiceBox<Territory> targetTerritoryChoiceBox = new ChoiceBox<>();
    targetTerritoryChoiceBox.setMaxWidth(Double.MAX_VALUE);
    targetTerritoryChoiceBox.setItems(targetTerritoryOptions);
    targetTerritoryChoiceBox.valueProperty().bindBidirectional(targetTerritory);
    targetTerritoryChoiceBox.setConverter(new TerritoryStringConverter());
    targetTerritoryChoiceBox.disableProperty().bind(sourceTerritory.isNull());
    HBox.setHgrow(targetTerritoryChoiceBox, Priority.ALWAYS);
    vBox.getChildren().addAll(targetTerritoryLabel, targetTerritoryChoiceBox);

    // Number of Attacking Troops selection
    HBox hbox = new HBox();
    Label numOfTroopLabel = new Label(TextResource.ATTACK_NUM_OF_TROOPS_LABEL);

    // Decrement Button
    Button decrementButton = renderButton("-");
    decrementButton.disableProperty().bind(Bindings.createBooleanBinding(
        () -> sourceTerritory.get() == null || numOfTroops.get() <= 1,
        sourceTerritory,
        numOfTroops
    ));
    decrementButton.setOnAction(actionEvent -> numOfTroops.set(numOfTroops.get() - 1));

    // Troop Count
    Label troopCount = new Label();
    troopCount.setStyle("-fx-alignment: center");
    troopCount.setTextAlignment(TextAlignment.CENTER);
    troopCount.setMinWidth(Region.USE_PREF_SIZE);
    troopCount.setMaxWidth(Double.MAX_VALUE);
    troopCount.setMinHeight(UI.UNIT * 3);
    troopCount.setTextAlignment(TextAlignment.CENTER);
    troopCount.setContentDisplay(ContentDisplay.CENTER);
    troopCount.textProperty().bind(numOfTroops.asString());
    HBox.setHgrow(troopCount, Priority.ALWAYS);

    // Increment Button
    Button incrementButton = renderButton("+");
    incrementButton.disableProperty().bind(Bindings.createBooleanBinding(
        () -> sourceTerritory.get() == null || numOfTroops.get() >= sourceTerritory.get().getNumberOfStationedTroops() - 1,
        sourceTerritory,
        numOfTroops
    ));
    incrementButton.setOnAction(mouseEvent -> numOfTroops.set(numOfTroops.get() + 1));
    hbox.getChildren().addAll(decrementButton, troopCount, incrementButton);
    vBox.getChildren().addAll(numOfTroopLabel, hbox);

    //UIUtil.debug(troopCount, vBox);
  }

  @Override
  protected EventHandler<ActionEvent> getSubmitButtonListener() {
    return actionEvent -> {
      Territory source = sourceTerritory.get();
      Territory target = targetTerritory.get();
      CombatResult result = GameEngine.get().performCombat(source, target, numOfTroops.get());
      // TODO: Display Result
      EventBus.emit(new CombatResultEvent(source, target, result));
      GameEngine.get().prepareTerritoryAttack(GameState.get().currentTurn());
    };
  }

  @Override
  protected String getSubmitButtonTitle() {
    return TextResource.ATTACK_ACTION;
  }

  @Override
  protected Color getSubmitButtonColor() {
    return Color.SUNSET_RED;
  }

  private void onAttackPhaseEvent(AttackPhaseEvent attackPhaseEvent) {
    numOfTroops.set(0);
    sourceTerritory.set(null);
    targetTerritory.set(null);
    sourceTerritoryOptions.setAll(
        new ArrayList<>(attackPhaseEvent.deployableTerritories()).stream()
            .filter(territory -> !GameEngine.get().getAttackableTerritories(territory).isEmpty())
            .sorted(Comparator.comparingInt(Territory::getNumberOfStationedTroops).reversed())
            .toArray(Territory[]::new)
    );
    targetTerritoryOptions.clear();
  }
}
