package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.event.TroopMovementEvent;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.Territory;
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
    this.gameMap = gameMap;
    HBox.setHgrow(this, Priority.ALWAYS);
    setPannable(true);
    Pane content = (Pane) getContent();
    Group hexGroup = (Group) content.getChildren().getFirst();
    populateMapTile(hexGroup, gameMap);
  }

  private void onTroopMovement(TroopMovementEvent troopMovementEvent) {
    Optional.ofNullable(troopMovementEvent.territoryFrom())
        .map(territory -> new Point2D(territory.getQ(), territory.getR()))
        .map(mapTileGrid::get)
        .ifPresent(MapTile::render);

    Optional.ofNullable(troopMovementEvent.territoryTo())
        .map(territory -> new Point2D(territory.getQ(), territory.getR()))
        .map(mapTileGrid::get)
        .ifPresent(MapTile::render);
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
}
