package edu.uph.learn.maharadja.ui.form;

import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.common.UIUtil;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.game.event.DraftPhaseEvent;
import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.factory.ButtonFactory;
import edu.uph.learn.maharadja.ui.factory.LabelFactory;
import edu.uph.learn.maharadja.ui.state.TileSelectionState;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DraftTroopForm extends BaseActionForm {
  private static final Logger LOG = LoggerFactory.getLogger(DraftTroopForm.class);

  private final VBox vbox;
  private final SimpleIntegerProperty draftedTroop = new SimpleIntegerProperty();
  private final Map<Territory, SimpleIntegerProperty> troopAssignment = new HashMap<>();
  private final ObservableList<Territory> sourceTerritoryOptions = TileSelectionState.get().getValidSources();
  private final Button submitButton;

  public DraftTroopForm() {
    super();
    EventBus.registerListener(DraftPhaseEvent.class, this::onDraftPhaseEvent);

    vbox = new VBox();
    vbox.setPadding(new Insets(UI.SMALL));
    vbox.setAlignment(Pos.CENTER);
    vbox.setMinWidth(this.getMinWidth());
    vbox.setPrefWidth(this.getPrefWidth());
    vbox.setMaxWidth(this.getMaxWidth());
    setContent(vbox);

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

  private void onDraftPhaseEvent(DraftPhaseEvent event) {
    LOG.info("[onDraftPhaseEvent] Player = {}, Number of Troops = {}", event.player().getUsername(), event.numOfTroops());
    resetForm();
    sourceTerritoryOptions.addAll(event.player().getTerritories());
    submitButton.disableProperty().bind(draftedTroop.isEqualTo(event.numOfTroops()).not());

    //region Num of Assignable Troop
    VBox numOfTroopVBox = new VBox();
    numOfTroopVBox.setAlignment(Pos.CENTER);
    Label label = new Label("Deployable Troops: ");
    label.setFont(UI.SMALL_FONT);
    label.setAlignment(Pos.CENTER);
    Label numOfTroop = new Label();
    numOfTroop.textProperty().bind(
        draftedTroop.subtract(event.numOfTroops())
            .multiply(-1)
            .asString()
    );
    numOfTroop.setFont(UI.LARGE_FONT);
    numOfTroop.setTextAlignment(TextAlignment.RIGHT);
    numOfTroopVBox.getChildren().addAll(label, numOfTroop);
    vbox.getChildren().add(numOfTroopVBox);
    //endregion

    //region Group by Region
    Map<Region, Integer> regionToTroop = new HashMap<>();
    Map<Region, List<Territory>> regionToTerritory = new HashMap<>();
    for (Territory territory : event.player().getTerritories()) {
      regionToTroop.putIfAbsent(territory.getRegion(), 0);
      regionToTroop.put(territory.getRegion(), regionToTroop.get(territory.getRegion()) + territory.getNumberOfStationedTroops());
      regionToTerritory.computeIfAbsent(territory.getRegion(), k -> new ArrayList<>());
      regionToTerritory.get(territory.getRegion()).add(territory);
    }
    //endregion

    for (Map.Entry<Region, List<Territory>> entry : regionToTerritory.entrySet()) {
      VBox regionVBox = new VBox();
      regionVBox.setPadding(new Insets(UI.UNIT, 0, UI.UNIT, 0));
      vbox.getChildren().add(regionVBox);

      //region Region Label
      Region region = entry.getKey();
      Label regionLabel = new Label(region.getName());
      regionLabel.setFont(UI.LARGE_FONT);
      regionLabel.setAlignment(Pos.CENTER);
      regionLabel.setTextAlignment(TextAlignment.CENTER);
      TextFlow textFlow = LabelFactory.createTextFlow(
          new Pair<>("(Owned Territory: ", false),
          new Pair<>(String.valueOf(entry.getValue().size()), true),
          new Pair<>(", Troop: ", false),
          new Pair<>(String.valueOf(regionToTroop.get(region)), true),
          new Pair<>(")", false)
      );
      textFlow.setTextAlignment(TextAlignment.CENTER);
      regionVBox.getChildren().addAll(regionLabel, textFlow);
      entry.getValue().sort(Comparator.comparingInt(Territory::getNumberOfStationedTroops).reversed());
      //endregion

      HBox regionHbox = new HBox();
      regionHbox.setPadding(new Insets(UI.SMALL, 0, UI.SMALL, 0));
      int idx = 0;
      for (Territory territory : entry.getValue()) {
        if (idx % 2 == 0) {
          regionHbox = new HBox();
          regionHbox.setAlignment(Pos.CENTER_LEFT);
          //UIUtil.debug(regionHbox);
          regionVBox.getChildren().add(regionHbox);
        }

        //region
        SimpleIntegerProperty currentTroopCount = new SimpleIntegerProperty(0);
        troopAssignment.put(territory, currentTroopCount);

        // Container
        VBox territoryVBox = new VBox();
        territoryVBox.setMinWidth(getWidth() / 2 - UI.UNIT);
        territoryVBox.setPadding(new Insets(0, UI.SMALL, 0, UI.SMALL));
        HBox.setHgrow(territoryVBox, Priority.ALWAYS);
        regionHbox.getChildren().add(territoryVBox);

        // Territory Label
        HBox firstRow = new HBox();
        firstRow.setAlignment(Pos.CENTER);
        territoryVBox.getChildren().add(firstRow);
        Label territoryNameLabel = new Label(territory.getName());
        territoryNameLabel.setMinWidth(getMinWidth() / 2 - UI.UNIT);
        territoryNameLabel.setMaxWidth(Double.MAX_VALUE);
        territoryNameLabel.setTextAlignment(TextAlignment.CENTER);
        territoryNameLabel.setContentDisplay(ContentDisplay.CENTER);
        firstRow.getChildren().add(territoryNameLabel);

        HBox secondRow = new HBox(UI.SMALL);
        secondRow.setAlignment(Pos.CENTER);
        territoryVBox.getChildren().addAll(secondRow);

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
        troopCount.setStyle("-fx-alignment: center");
        troopCount.setTextAlignment(TextAlignment.CENTER);
        troopCount.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
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
        //endregion

        secondRow.getChildren().addAll(decrementButton, troopCount, incrementButton);
        idx++;
      }

      //region fake to fill space
      if (idx % 2 == 1) {
        VBox fakeBox = new VBox();
        fakeBox.setVisible(false);
        fakeBox.setMinWidth(getWidth() / 2);
        fakeBox.setPadding(new Insets(0, UI.SMALL, 0, UI.SMALL));
        HBox.setHgrow(fakeBox, Priority.ALWAYS);
        regionHbox.getChildren().add(fakeBox);
        HBox firstRow = new HBox();
        fakeBox.getChildren().add(firstRow);
        Label territoryNameLabel = new Label("foo");
        territoryNameLabel.setMinWidth(getMinWidth() / 2 - UI.UNIT);
        territoryNameLabel.setMaxWidth(Double.MAX_VALUE);
        territoryNameLabel.setTextAlignment(TextAlignment.CENTER);
        territoryNameLabel.setContentDisplay(ContentDisplay.CENTER);
        territoryNameLabel.setVisible(false);
        firstRow.getChildren().add(territoryNameLabel);
        HBox fakeSecondRow = new HBox(UI.SMALL);
        fakeSecondRow.setAlignment(Pos.CENTER);
        fakeBox.getChildren().addAll(fakeSecondRow);
        Button fakeButton1 = renderButton("-");
        fakeButton1.setVisible(false);
        Label fakeLabel = new Label("0");
        fakeLabel.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        fakeLabel.setMaxWidth(Double.MAX_VALUE);
        fakeLabel.setMinHeight(UI.UNIT * 3);
        fakeLabel.setVisible(false);
        HBox.setHgrow(fakeLabel, Priority.ALWAYS);
        Button fakeButton2 = renderButton("+");
        fakeButton2.setVisible(false);
        fakeSecondRow.getChildren().addAll(fakeButton1, fakeLabel, fakeButton2);
      }
      //endregion
    }

    setVisible(true);
  }

  private void resetForm() {
    vbox.getChildren().clear();
    draftedTroop.set(0);
    troopAssignment.clear();
  }

  public Button getSubmitButton() {
    return submitButton;
  }
}
