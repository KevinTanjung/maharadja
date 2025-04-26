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
import edu.uph.learn.maharadja.ui.event.TerritorySelectedEvent;
import edu.uph.learn.maharadja.ui.event.TerritorySelectedEvent.SelectionType;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.Objects;

public class AttackTerritoryForm extends BaseActionForm {
  private final Button submitButton;
  private final Button endButton;
  private final ObjectProperty<Territory> sourceTerritory = new SimpleObjectProperty<>();
  private final ObjectProperty<Territory> targetTerritory = new SimpleObjectProperty<>();
  private final SimpleIntegerProperty numOfTroops = new SimpleIntegerProperty();
  private final ObservableList<Territory> sourceTerritoryOptions = FXCollections.observableArrayList();
  private final ObservableList<Territory> targetTerritoryOptions = FXCollections.observableArrayList();

  //public AttackTroopForm(ObjectProperty<MapTile> sourceTile,
  //                       ObjectProperty<MapTile> targetTile) {
  public AttackTerritoryForm() {
    super();
    EventBus.registerListener(AttackPhaseEvent.class, this::onAttackPhaseEvent);
    EventBus.registerListener(TerritorySelectedEvent.class, this::onTerritorySelectedEvent);

    double buttonWidth = (UI.TAB_WIDTH / 2) - UI.UNIT;
    submitButton = ButtonFactory.create(
        TextResource.ATTACK_ACTION,
        buttonWidth,
        Color.IVORY_WHITE,
        Color.SUNSET_RED
    );
    submitButton.setOnAction(actionEvent -> {
      Territory source = sourceTerritory.get();
      Territory target = targetTerritory.get();
      CombatResult result = GameEngine.get().performCombat(source, target, numOfTroops.get());
      // TODO: Display Result
      EventBus.emit(new CombatResultEvent(source, target, result));
      GameEngine.get().prepareTerritoryAttack(GameState.get().currentTurn());
    });
    endButton = ButtonFactory.create(
        TextResource.ATTACK_END_ACTION,
        buttonWidth,
        Color.IVORY_WHITE,
        Color.GUNMETAL_GREY
    );
    endButton.setOnAction(actionEvent -> GameEngine.get().nextPhase());

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
    sourceTerritoryChoiceBox.setOnAction(event -> {
      Territory selectedItem = sourceTerritoryChoiceBox.getSelectionModel().getSelectedItem();
      if (selectedItem == null) {
        return;
      }
      EventBus.emit(new TerritorySelectedEvent(this, selectedItem, SelectionType.FROM));

      List<Territory> attackableTerritories = GameEngine.get().getAttackableTerritories(
          selectedItem,
          GameState.get().currentTurn()
      );
      numOfTroops.setValue(selectedItem.getNumberOfStationedTroops() - 1);
      targetTerritoryOptions.setAll(attackableTerritories.toArray(Territory[]::new));
      // TODO: highlight attackable territories
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
    targetTerritoryChoiceBox.setOnAction(event -> {
      Territory selectedItem = targetTerritoryChoiceBox.getSelectionModel().getSelectedItem();
      if (selectedItem == null) {
        return;
      }
      EventBus.emit(new TerritorySelectedEvent(this, selectedItem, SelectionType.TO));
      // TODO: highlight attackable territories
    });
    targetTerritoryChoiceBox.disableProperty().bind(sourceTerritory.isNull());
    HBox.setHgrow(targetTerritoryChoiceBox, Priority.ALWAYS);
    vBox.getChildren().addAll(targetTerritoryLabel, targetTerritoryChoiceBox);

    // Number of Attacking Troops selection
    HBox hbox = new HBox();
    Label numOfTroopLabel = new Label(TextResource.ATTACK_NUM_OF_TROOPS_LABEL);

    // Decrement Button
    Button decrementButton = renderButton("-");
    decrementButton.disableProperty().bind(Bindings.createBooleanBinding(
        () -> {
          if (sourceTerritory.get() == null) {
            return true;
          }
          return numOfTroops.get() <= 1;
        },
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
        () -> {
          if (sourceTerritory.get() == null) {
            return true;
          }
          int deployableTroops = sourceTerritory.get().getNumberOfStationedTroops() - 1;
          return numOfTroops.get() >= deployableTroops;
        },
        sourceTerritory,
        numOfTroops
    ));
    incrementButton.setOnAction(mouseEvent -> numOfTroops.set(numOfTroops.get() + 1));
    hbox.getChildren().addAll(decrementButton, troopCount, incrementButton);
    vBox.getChildren().addAll(numOfTroopLabel, hbox);

    //UIUtil.debug(troopCount, vBox);
  }

  private void onAttackPhaseEvent(AttackPhaseEvent attackPhaseEvent) {
    sourceTerritory.set(null);
    targetTerritory.set(null);
    numOfTroops.set(0);
    targetTerritoryOptions.clear();

    List<Territory> deployableTerritories = new ArrayList<>(attackPhaseEvent.deployableTerritories());
    deployableTerritories.sort(Comparator.comparingInt(Territory::getNumberOfStationedTroops).reversed());
    sourceTerritoryOptions.setAll(deployableTerritories.toArray(Territory[]::new));
  }

  private Button renderButton(String label) {
    return ButtonFactory.square(label, UI.UNIT * 3, Color.VOLCANIC_BLACK, Color.SLATE_GRAY);
  }

  private void onTerritorySelectedEvent(TerritorySelectedEvent territorySelectedEvent) {
    // ignore event during non-attacking phase or when request originates from here
    if (GameState.get().currentPhase() != TurnPhase.ATTACK || territorySelectedEvent.eventSource() == this) {
      return;
    }

    if (territorySelectedEvent.type() == SelectionType.FROM
        && Objects.equals(GameState.get().currentTurn(), territorySelectedEvent.territory().getOwner())
    ) {
      sourceTerritory.set(territorySelectedEvent.territory());
      return;
    }

    if (territorySelectedEvent.type() == SelectionType.TO
        && sourceTerritory.get() != null
        && GameEngine.get().isAttackable(sourceTerritory.get(), territorySelectedEvent.territory())
    ) {
      targetTerritory.set(territorySelectedEvent.territory());
    }
  }

  public Button getSubmitButton() {
    return submitButton;
  }

  public Button getEndButton() {
    return endButton;
  }
}
