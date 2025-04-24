package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.TileType;
import edu.uph.learn.maharadja.ui.event.MapTileSelectedEvent;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import static edu.uph.learn.maharadja.common.UI.HEX_SIZE;

/**
 * Inspired by <a href="https://www.redblobgames.com/grids/hexagons-v1">source</a>
 */
public class MapTile extends Polygon {
  private final Territory territory;
  private final Region region;
  private final TileType tileType;
  private final int q;
  private final int r;
  //private final Text label;

  public MapTile(int q, int r) {
    this(null, null, q, r, TileType.WATER);
  }

  public MapTile(Territory territory) {
    this(territory, territory.getRegion(), territory.getQ(), territory.getR(), TileType.TERRITORY);
  }

  private MapTile(Territory territory, Region region, int q, int r, TileType tileType) {
    super();
    this.q = q;
    this.r = r;
    this.territory = territory;
    this.region = region;
    this.tileType = tileType;

    // https://www.redblobgames.com/grids/hexagons-v1/#angles
    for (int i = 0; i < 6; i++) {
      double angle = Math.toRadians(60 * i + 30);
      getPoints().addAll(HEX_SIZE * Math.cos(angle), HEX_SIZE * Math.sin(angle));
    }
    // https://www.redblobgames.com/grids/hexagons-v1/#hex-to-pixel-offset
    double x = HEX_SIZE * Math.sqrt(3) * (q + 0.5 * (r & 1));
    double y = HEX_SIZE * 3 / 2 * r;
    setTranslateX(x);
    setTranslateY(y);

    render();
  }

  public void render() {
    if (region == null) {
      setStroke(javafx.scene.paint.Color.web(Color.SKY_BLUE.toHex(), 0.3));
      setFill(Color.ALICE_BLUE.get());
      //label = null;
    } else {
      setStrokeBasedOnRegion();
      setFillBasedOnTerritoryOwner();
      setMouseEvent();
      //label = renderLabel();
      Tooltip tooltip = new Tooltip(String.format("Territory: %s\nRegion: %s\n%s",
          territory.getName(),
          region.getName(),
          territory.getOwner() == null ? "" : String.format("Ruler: %s", territory.getOwner().getUsername())
      ));
      tooltip.setShowDelay(Duration.millis(50));
      tooltip.setHideDelay(Duration.millis(100));
      Tooltip.install(this, tooltip);
    }
  }

  public TileType getTileType() {
    return tileType;
  }

  public Territory getTerritory() {
    return territory;
  }

  public Region getRegion() {
    return region;
  }

  private void setStrokeBasedOnRegion() {
    if (region.getColor() == null) {
      setStroke(Color.VOLCANIC_BLACK.get());
    } else {
      setStroke(region.getColor().get());
    }
  }

  private void setFillBasedOnTerritoryOwner() {
    if (territory.getOwner() == null) {
      setFill(Color.IVORY_WHITE.get());
    } else {
      Color playerColor = territory.getOwner().getColor();
      javafx.scene.paint.Color color = javafx.scene.paint.Color.web(playerColor.toHex(), 0.5);
      setFill(color);
    }
  }

  private void setMouseEvent() {
    setOnMouseEntered(e -> {
      setStrokeWidth(2);
      setCursor(Cursor.HAND);
    });
    setOnMouseExited(e -> {
      setStrokeWidth(1);
      setCursor(Cursor.DEFAULT);
    });
    setOnMouseClicked(e -> EventBus.emit(new MapTileSelectedEvent(this)));
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof MapTile mapTile)) {
      return false;
    }

    return q == mapTile.q && r == mapTile.r;
  }

  @Override
  public int hashCode() {
    int result = q;
    result = 31 * result + r;
    return result;
  }
}
