package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.Event;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.event.FortifyPhaseEvent;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.TextResource;
import edu.uph.learn.maharadja.ui.event.TerritoryHighlightedEvent;
import edu.uph.learn.maharadja.ui.event.TerritorySelectedEvent;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class FortifyTerritoryForm extends BaseActionForm {
  private static final Logger LOG = LoggerFactory.getLogger(FortifyTerritoryForm.class);

  private final Button submitButton;
  private final ObjectProperty<Territory> sourceTerritory = new SimpleObjectProperty<>();
  private final ObjectProperty<Territory> targetTerritory = new SimpleObjectProperty<>();
  private final SimpleIntegerProperty numOfTroops = new SimpleIntegerProperty();
  private final ObservableList<Territory> sourceTerritoryOptions = FXCollections.observableArrayList();
  private final ObservableList<Territory> targetTerritoryOptions = FXCollections.observableArrayList();

  public FortifyTerritoryForm() {
    super();
    EventBus.registerListener(FortifyPhaseEvent.class, this::onFortifyPhaseEvent);
    EventBus.registerListener(TerritorySelectedEvent.class, this::onTerritorySelectedEvent);

    setBackground(Background.fill(Color.IVORY_WHITE.get()));
    setFitToWidth(true);
    setFitToHeight(true);
    setHbarPolicy(ScrollBarPolicy.NEVER);

    // Fortify Button
    submitButton = ButtonFactory.create(
        TextResource.FORTIFY_ACTION,
        UI.TAB_WIDTH - UI.UNIT,
        Color.VOLCANIC_BLACK,
        Color.SKY_BLUE
    );
    submitButton.setOnAction(actionEvent -> {
      List<Territory> deploymentStep = GameEngine.get().fortifyTerritory(
          sourceTerritory.get(),
          targetTerritory.get(),
          numOfTroops.get()
      );
      LOG.info("Fortify success with deploymentStep {}", deploymentStep.stream().map(Territory::getName).toList());
    });


    VBox vBox = new VBox(UI.SMALL);
    vBox.setMaxWidth(UI.TAB_WIDTH - UI.UNIT);
    vBox.setBackground(Background.fill(Color.IVORY_WHITE.get()));
    setContent(vBox);

    // Source Territory selection
    Label sourceTerritoryLabel = new Label(TextResource.FORTIFY_SOURCE_TERRITORY_LABEL);
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
      EventBus.emit(new TerritorySelectedEvent(this, selectedItem, TerritorySelectedEvent.SelectionType.FROM));
      List<Territory> fortifiableTerritories = GameEngine.get().getFortifiableTerritories(selectedItem);
      numOfTroops.setValue(1);
      targetTerritoryOptions.setAll(fortifiableTerritories.toArray(Territory[]::new));
      EventBus.emit(new TerritoryHighlightedEvent(Set.copyOf(fortifiableTerritories)));
    });
    HBox.setHgrow(sourceTerritoryChoiceBox, Priority.ALWAYS);
    vBox.getChildren().addAll(sourceTerritoryLabel, sourceTerritoryChoiceBox);

    // Target Territory selection
    Label targetTerritoryLabel = new Label(TextResource.FORTIFY_TARGET_TERRITORY_LABEL);
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
      EventBus.emit(new TerritorySelectedEvent(this, selectedItem, TerritorySelectedEvent.SelectionType.TO));
    });
    targetTerritoryChoiceBox.disableProperty().bind(sourceTerritory.isNull());
    HBox.setHgrow(targetTerritoryChoiceBox, Priority.ALWAYS);
    vBox.getChildren().addAll(targetTerritoryLabel, targetTerritoryChoiceBox);

    // Number of Attacking Troops selection
    HBox hbox = new HBox();
    Label numOfTroopLabel = new Label(TextResource.FORTIFY_NUM_OF_TROOPS_LABEL);

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
  }

  private void onFortifyPhaseEvent(FortifyPhaseEvent fortifyPhaseEvent) {
    sourceTerritory.set(null);
    targetTerritory.set(null);
    numOfTroops.set(0);
    targetTerritoryOptions.clear();

    List<Territory> deployableTerritory = new ArrayList<>(fortifyPhaseEvent.deployableTerritory())
        .stream()
        .filter(territory -> !GameEngine.get().getFortifiableTerritories(territory).isEmpty())
        .sorted(Comparator.comparingInt(Territory::getNumberOfStationedTroops).reversed())
        .toList();
    // TODO: filter non "deployable" territory since there are no friendly neighbor
    sourceTerritoryOptions.setAll(deployableTerritory.toArray(Territory[]::new));
  }

  private void onTerritorySelectedEvent(TerritorySelectedEvent territorySelectedEvent) {
    // TODO impl
  }

  public Button getSubmitButton() {
    return submitButton;
  }

  private Button renderButton(String label) {
    return ButtonFactory.square(label, UI.UNIT * 3, Color.VOLCANIC_BLACK, Color.SLATE_GRAY);
  }
}
