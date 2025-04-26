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
import edu.uph.learn.maharadja.ui.event.TerritoryHighlightedEvent;
import edu.uph.learn.maharadja.ui.event.TerritorySelectedEvent;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

@SuppressWarnings( {"FieldCanBeLocal", "unused", "MismatchedQueryAndUpdateOfCollection"})
public class MapPane extends ScrollPane {
  private final GameMap gameMap;
  private final Map<Point2D, MapTile> mapTileGrid = new HashMap<>();
  private final Stack<Territory> lastSelectedTerritoryStack = new Stack<>();
  private final Set<Territory> lastHighlightedTerritory = new HashSet<>();

  public MapPane(GameMap gameMap) {
    super(new Pane(new Group()));
    EventBus.registerListener(TroopMovementEvent.class, this::onTroopMovement);
    EventBus.registerListener(GamePhaseEvent.class, this::onGamePhaseEvent);
    EventBus.registerListener(CombatResultEvent.class, this::onCombatResultEvent);
    EventBus.registerListener(TerritoryOccupiedEvent.class, this::onTerritoryOccupiedEvent);
    EventBus.registerListener(TerritorySelectedEvent.class, this::onTerritorySelectedEvent);
    EventBus.registerListener(TerritoryHighlightedEvent.class, this::onTerritoryHighlightedEvent);
    this.gameMap = gameMap;
    HBox.setHgrow(this, Priority.ALWAYS);
    setPannable(true);
    Pane content = (Pane) getContent();
    Group hexGroup = (Group) content.getChildren().getFirst();
    populateMapTile(hexGroup, gameMap);
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

  private void onTerritorySelectedEvent(TerritorySelectedEvent territorySelectedEvent) {
    Territory territory = territorySelectedEvent.territory();
    Optional.ofNullable(mapTileGrid.get(territory.getPoint()))
        .ifPresent(mapTile -> {
          if (mapTile.selectedProperty().get()) {
            clearSelection(mapTile);
            return;
          }
          TurnPhase currentPhase = GameState.get().currentPhase();
          if (currentPhase != TurnPhase.ATTACK && currentPhase != TurnPhase.FORTIFY) {
            clearSelection();
          }
          if (territorySelectedEvent.type() == TerritorySelectedEvent.SelectionType.FROM) {
            clearSelection();
            highlightTile(false);
          } else if (territorySelectedEvent.type() == TerritorySelectedEvent.SelectionType.TO) {
            if (lastSelectedTerritoryStack.size() >= 2) {
              Territory lastSelectedTerritory = lastSelectedTerritoryStack.pop();
              Optional.ofNullable(mapTileGrid.get(lastSelectedTerritory.getPoint()))
                  .ifPresent(lastSelectedMapTile -> lastSelectedMapTile.selectedProperty().set(false));
            }
          }
          selectTile(mapTile, territory);
        });
  }

  private void onTerritoryHighlightedEvent(TerritoryHighlightedEvent territoryHighlightedEvent) {
    clearHighlight();
    lastHighlightedTerritory.addAll(territoryHighlightedEvent.territories());
    highlightTile();
  }

  private void onGamePhaseEvent(GamePhaseEvent gamePhaseEvent) {
    clearSelection();
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

  private void selectTile(MapTile mapTile, Territory territory) {
    lastSelectedTerritoryStack.add(territory);
    mapTile.selectedProperty().set(true);
  }

  private void clearSelection() {
    clearSelection(null);
  }

  private void clearSelection(MapTile mapTile) {
    lastSelectedTerritoryStack.stream()
        .map(Territory::getPoint)
        .map(mapTileGrid::get)
        .filter(t -> mapTile == null || Objects.equals(mapTile, t))
        .forEach(selectedMapTile -> selectedMapTile.selectedProperty().set(false));
    if (mapTile == null) {
      lastSelectedTerritoryStack.clear();
    } else {
      lastSelectedTerritoryStack.remove(mapTile.getTerritory());
    }
  }

  private void clearHighlight() {
    highlightTile(false);
  }

  private void highlightTile() {
    highlightTile(true);
  }

  private void highlightTile(boolean highlighted) {
    lastHighlightedTerritory.stream()
        .map(Territory::getPoint)
        .map(mapTileGrid::get)
        .map(MapTile::highlightedProperty)
        .forEach(property -> property.set(highlighted));
    if (!highlighted) {
      lastHighlightedTerritory.clear();
    }
  }
  //endregion
}
