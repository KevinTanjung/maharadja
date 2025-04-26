package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.event.TerritoryOccupiedEvent;
import edu.uph.learn.maharadja.game.event.TroopMovementEvent;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.event.CombatResultEvent;
import edu.uph.learn.maharadja.ui.event.TerritorySelectedEvent;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings( {"FieldCanBeLocal", "unused", "MismatchedQueryAndUpdateOfCollection"})
public class MapPane extends ScrollPane {
  private final GameMap gameMap;
  private final Map<Point2D, MapTile> mapTileGrid = new HashMap<>();

  public MapPane(GameMap gameMap) {
    super(new Pane(new Group()));
    EventBus.registerListener(TroopMovementEvent.class, this::onTroopMovement);
    EventBus.registerListener(CombatResultEvent.class, this::onCombatResultEvent);
    EventBus.registerListener(TerritoryOccupiedEvent.class, this::onTerritoryOccupiedEvent);
    EventBus.registerListener(TerritorySelectedEvent.class, this::onTerritorySelectedEvent);
    this.gameMap = gameMap;
    HBox.setHgrow(this, Priority.ALWAYS);
    setPannable(true);
    Pane content = (Pane) getContent();
    Group hexGroup = (Group) content.getChildren().getFirst();
    populateMapTile(hexGroup, gameMap);
  }

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

  private void updateTileTerritory(Territory territory) {
    Optional.ofNullable(territory)
        .map(t -> new Point2D(t.getQ(), t.getR()))
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
          //hexGroup.getChildren().add(territoryTile.getLabel());
          mapTileGrid.put(point, territoryTile);
          continue;
        }

        MapTile waterTile = new MapTile(q, r);
        hexGroup.getChildren().add(waterTile);
        mapTileGrid.put(point, waterTile);
      }
    }
  }

  private void onTerritorySelectedEvent(
      TerritorySelectedEvent territorySelectedEvent
  ) {
    // do nothing
  }
}
