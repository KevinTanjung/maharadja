package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.Territory;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MapPane extends ScrollPane {
  private static final Logger log = LoggerFactory.getLogger(MapPane.class);

  private final GameMap gameMap;
  private final Map<Point2D, MapTile> tileMap = new HashMap<>();

  public MapPane(GameMap gameMap) {
    super(new Pane(new Group()));
    this.gameMap = gameMap;
    setPannable(true);
    Pane content = (Pane) getContent();
    Group hexGroup = (Group) content.getChildren().getFirst();
    populateMapTile(hexGroup, gameMap);
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
          tileMap.put(point, territoryTile);
          continue;
        }

        MapTile waterTile = new MapTile(q, r);
        hexGroup.getChildren().add(waterTile);
        tileMap.put(point, waterTile);
      }
    }
  }
}
