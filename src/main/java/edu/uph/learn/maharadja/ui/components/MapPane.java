package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.TurnPhase;
import edu.uph.learn.maharadja.game.event.GamePhaseEvent;
import edu.uph.learn.maharadja.game.event.TerritoryOccupiedEvent;
import edu.uph.learn.maharadja.game.event.TroopMovementEvent;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.event.CombatResultEvent;
import edu.uph.learn.maharadja.ui.state.TileSelectionState;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

@SuppressWarnings( {"FieldCanBeLocal", "unused", "MismatchedQueryAndUpdateOfCollection"})
public class MapPane extends ScrollPane {
  private static final Logger LOG = LoggerFactory.getLogger(MapPane.class);
  private final GameMap gameMap;
  private final Map<Point2D, MapTile> mapTileGrid = new HashMap<>();
  private final Map<Territory, Timeline> highlightAnimation = new HashMap<>();
  private final ObservableList<Territory> sourceTerritoryOptions = TileSelectionState.get().getValidSources();
  private final ObservableList<Territory> targetTerritoryOptions = TileSelectionState.get().getValidTargets();
  private final ObjectProperty<Territory> selectedSource = TileSelectionState.get().selectedSourceProperty();
  private final ObjectProperty<Territory> selectedTarget = TileSelectionState.get().selectedTargetProperty();
  private final ObjectProperty<Territory> selectedDetail = TileSelectionState.get().selectedDetailProperty();
  private final EventHandler<? super MouseEvent> onClickMapTile;

  public MapPane(GameMap gameMap) {
    super(new Pane(new Group()));
    EventBus.registerListener(TroopMovementEvent.class, this::onTroopMovement);
    EventBus.registerListener(GamePhaseEvent.class, this::onGamePhaseEvent);
    EventBus.registerListener(CombatResultEvent.class, this::onCombatResultEvent);
    EventBus.registerListener(TerritoryOccupiedEvent.class, this::onTerritoryOccupiedEvent);
    this.gameMap = gameMap;
    onClickMapTile = e -> {
      MapTile clickedMapTile = (MapTile) e.getSource();
      Territory clickedTerritory = clickedMapTile.getTerritory();
      if (Objects.equals(selectedSource.get(), clickedTerritory)) {
        selectedSource.set(null);
      } else if (Objects.equals(selectedTarget.get(), clickedTerritory)) {
        selectedTarget.set(null);
      } else if (Objects.equals(selectedDetail.get(), clickedTerritory)) {
        selectedDetail.set(null);
      } else if (sourceTerritoryOptions.contains(clickedTerritory)
          && !targetTerritoryOptions.contains(clickedTerritory)
      ) {
        selectedSource.set(clickedTerritory);
      } else if (targetTerritoryOptions.contains(clickedTerritory)) {
        selectedTarget.set(clickedTerritory);
      } else {
        selectedDetail.set(clickedTerritory);
      }
    };
    HBox.setHgrow(this, Priority.ALWAYS);
    setPannable(true);
    Pane content = (Pane) getContent();
    Group hexGroup = (Group) content.getChildren().getFirst();
    populateMapTile(hexGroup, gameMap);

    selectedSource.addListener((obs, oldVal, newVal) -> {
      LOG.info("Selected Source: {} -> {}", printTerritory(oldVal), printTerritory(newVal));
      if (GameState.get().currentPhase() != TurnPhase.DRAFT) {
        clearAll();
      }
      selectTile(oldVal, false);
      selectTile(newVal, true);
    });
    selectedTarget.addListener((obs, oldVal, newVal) -> {
      LOG.info("Selected Target: {} -> {}", printTerritory(oldVal), printTerritory(newVal));
      selectTile(oldVal, false);
      selectTile(newVal, true);
    });
    sourceTerritoryOptions.addListener((ListChangeListener<Territory>) change -> {
      clearAll();
      sourceTerritoryOptions.forEach(this::highlightTile);
    });
    targetTerritoryOptions.addListener((ListChangeListener<Territory>) change -> {
      clearAll();
      targetTerritoryOptions.forEach(this::highlightTile);
    });
  }

  //region event listeners
  private void onCombatResultEvent(CombatResultEvent combatResultEvent) {
    if (combatResultEvent.result().attackerLost() > 0) {
      updateTileTerritory(combatResultEvent.attacker());
    }
    if (combatResultEvent.result().defenderLost() > 0) {
      updateTileTerritory(combatResultEvent.defender());
    }
  }

  private void onTroopMovement(TroopMovementEvent troopMovementEvent) {
    updateTileTerritory(troopMovementEvent.territoryFrom());
    updateTileTerritory(troopMovementEvent.territoryTo());
  }

  private void onTerritoryOccupiedEvent(TerritoryOccupiedEvent territoryOccupiedEvent) {
    Point2D point2D = new Point2D(
        territoryOccupiedEvent.territory().getQ(),
        territoryOccupiedEvent.territory().getR()
    );
    Optional.ofNullable(mapTileGrid.get(point2D))
        .map(MapTile::ownerProperty)
        .ifPresent(prop -> prop.set(territoryOccupiedEvent.player()));
  }

  private void onGamePhaseEvent(GamePhaseEvent gamePhaseEvent) {
    selectedSource.set(null);
    selectedTarget.set(null);
    clearAll();
  }
  //endregion

  //region helpers
  private void updateTileTerritory(Territory territory) {
    Optional.ofNullable(territory)
        .map(Territory::getPoint)
        .map(mapTileGrid::get)
        .ifPresent(mapTile -> {
          mapTile.numberOfTroopsProperty().set(territory.getNumberOfStationedTroops());
          mapTile.ownerProperty().set(territory.getOwner());
        });
  }

  private void populateMapTile(Group hexGroup, GameMap gameMap) {
    Map<Point2D, Territory> grid = gameMap.getTerritoryMap();
    for (int r = 0; r < gameMap.getR(); r++) {
      for (int q = 0; q < gameMap.getQ(); q++) {
        Point2D point = new Point2D(q, r);
        Territory territory = grid.get(point);
        if (territory != null) {
          MapTile territoryTile = new MapTile(territory);
          territoryTile.setOnMouseClicked(onClickMapTile);
          hexGroup.getChildren().add(territoryTile);
          mapTileGrid.put(point, territoryTile);
          continue;
        }
        MapTile waterTile = new MapTile(q, r);
        hexGroup.getChildren().add(waterTile);
        mapTileGrid.put(point, waterTile);
      }
    }
  }

  private void selectTile(Territory territory, boolean selected) {
    Optional.ofNullable(territory)
        .map(Territory::getPoint)
        .map(mapTileGrid::get)
        .ifPresent(mapTile -> mapTile.selectedProperty().set(selected));
  }

  private void clearHighlight(Territory territory) {
    highlightTile(territory, false);
  }

  private void highlightTile(Territory territory) {
    highlightTile(territory, true);
  }

  private void highlightTile(Territory territory, boolean highlighted) {
    Optional.ofNullable(territory)
        .map(Territory::getPoint)
        .map(mapTileGrid::get)
        .ifPresent(mapTile -> {
          mapTile.highlightedProperty().set(highlighted);
          if (highlighted) {
            highlightAnimation.put(territory, getHighlightAnimation(mapTile));
          } else {
            Optional.ofNullable(highlightAnimation.get(territory))
                .ifPresent(Timeline::stop);
            highlightAnimation.remove(territory);
          }
        });
  }

  private void clearAll() {
    for (Map.Entry<Point2D, MapTile> entry : mapTileGrid.entrySet()) {
      highlightTile(entry.getValue().getTerritory(), false);
    }
  }

  private Timeline getHighlightAnimation(MapTile mapTile) {
    if (mapTile == null) {
      return null;
    }

    Color color = (Color) mapTile.fillProperty().get();
    double originalOpacity = color.getOpacity();
    Timeline timeline = new Timeline(
        new KeyFrame(
            Duration.ZERO,
            new KeyValue(mapTile.strokeWidthProperty(), 3.0),
            new KeyValue(mapTile.fillProperty(), Color.color(color.getRed(), color.getGreen(), color.getBlue(), originalOpacity))
        ),
        new KeyFrame(
            Duration.seconds(1),
            new KeyValue(mapTile.strokeWidthProperty(), 2.0),
            new KeyValue(mapTile.fillProperty(), Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.7))
        )
    );
    timeline.setAutoReverse(true);
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
    return timeline;
  }

  private static String printTerritory(Territory oldVal) {
    return Optional.ofNullable(oldVal).map(Territory::getName).orElse("NULL");
  }

  private Object printTerritory(List<? extends Territory> territoryList) {
    StringBuilder print = new StringBuilder();
    for (Territory territory : territoryList) {
      print.append(territory.getName());
      print.append(",");
    }
    print.deleteCharAt(print.length() - 1);
    return print.toString();
  }
  //endregion
}
