package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.Territory;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

import java.util.Map;

import static edu.uph.learn.maharadja.common.UI.MAX_SCALE;
import static edu.uph.learn.maharadja.common.UI.MIN_SCALE;

public class MapPane extends ScrollPane {
  public MapPane(GameMap gameMap) {
    super(new Pane(new Group()));
    setPannable(true);
    Pane content = (Pane) getContent();
    Group hexGroup = (Group) content.getChildren().getFirst();

    populateMap(hexGroup, gameMap);

    content.setOnZoom(event -> {
      double zoomFactor = event.getZoomFactor();
      double newScaleX = hexGroup.getScaleX() * zoomFactor;
      double newScaleY = hexGroup.getScaleY() * zoomFactor;
      if (newScaleX >= MIN_SCALE && newScaleX <= MAX_SCALE) {
        hexGroup.setScaleX(newScaleX);
        hexGroup.setScaleY(newScaleY);
      }
    });
  }

  private void populateMap(Group hexGroup, GameMap gameMap) {
    Map<Point2D, Territory> grid = gameMap.getTerritoryMap();

    for (int r = 0; r < gameMap.getR(); r++) {
      for (int q = 0; q < gameMap.getQ(); q++) {
        Territory territory = grid.get(new Point2D(q, r));
        MapTile tile;
        if (territory != null) {
          tile = new MapTile(territory);
          hexGroup.getChildren().add(tile);
          //hexGroup.getChildren().add(tile.getLabel());
        } else {
          hexGroup.getChildren().add(new MapTile(q, r));
        }
      }
    }
  }
}
