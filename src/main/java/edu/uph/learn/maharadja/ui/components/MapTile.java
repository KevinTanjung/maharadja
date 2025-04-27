package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.player.Player;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.TileType;
import edu.uph.learn.maharadja.ui.factory.LabelFactory;
import edu.uph.learn.maharadja.common.UIUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static edu.uph.learn.maharadja.common.UI.HEX_SIZE;

/**
 * Inspired by <a href="https://www.redblobgames.com/grids/hexagons-v1">source</a>
 */
public class MapTile extends Group {
  private static final Logger log = LoggerFactory.getLogger(MapTile.class);
  private final ObjectProperty<Player> owner = new SimpleObjectProperty<>(null);
  private final SimpleIntegerProperty numberOfTroops = new SimpleIntegerProperty(1);
  private final SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
  private final SimpleBooleanProperty highlighted = new SimpleBooleanProperty(false);
  private final Territory territory;
  private final int q;
  private final int r;
  private final TileType tileType;
  private final Polygon hex;

  public MapTile(int q, int r) {
    this(null, q, r, TileType.WATER);
  }

  public MapTile(Territory territory) {
    this(territory, territory.getQ(), territory.getR(), TileType.TERRITORY);
  }

  private MapTile(Territory territory, int q, int r, TileType tileType) {
    super();
    this.territory = territory;
    this.q = q;
    this.r = r;
    this.tileType = tileType;

    // https://www.redblobgames.com/grids/hexagons-v1/#angles
    hex = new Polygon();
    for (int i = 0; i < 6; i++) {
      double angle = Math.toRadians(60 * i + 30);
      hex.getPoints().addAll(HEX_SIZE * Math.cos(angle), HEX_SIZE * Math.sin(angle));
    }
    // https://www.redblobgames.com/grids/hexagons-v1/#hex-to-pixel-offset
    double x = HEX_SIZE * Math.sqrt(3) * (q + 0.5 * (r & 1));
    double y = HEX_SIZE * 3 / 2 * r;
    setTranslateX(x);
    setTranslateY(y);

    if (territory == null) {
      hex.setStroke(UIUtil.alpha(Color.SKY_BLUE, 0.3));
      hex.setFill(Color.ALICE_BLUE.get());
      getChildren().add(hex);
      return;
    }

    hex.setStroke(territory.getRegion().getColor().get());
    owner.addListener((obs, oldVal, newVal) -> fillBackground(newVal, selected.get(), highlighted.get()));
    selected.addListener((obs, oldVal, newVal) -> fillBackground(owner.get(), newVal, highlighted.get()));
    highlighted.addListener((obs, oldVal, newVal) -> fillBackground(owner.get(), selected.get(), newVal));
    setMouseEvent();
    renderToolTip();
    Label label = LabelFactory.troopLabel();
    label.textProperty().bind(numberOfTroops.asString());
    getChildren().addAll(hex, label);

    selectedProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal) {
        hex.setStrokeWidth(4.5);
        label.setStyle(LabelFactory.troopLabelStyle(Color.IMPERIAL_GOLD, Color.VOLCANIC_BLACK));
      } else {
        label.setStyle(LabelFactory.troopLabelStyle(Color.VOLCANIC_BLACK, Color.IVORY_WHITE));
        if (highlighted.get()) {
          hex.setStrokeWidth(3.0);
        } else {
          hex.setStrokeWidth(1.0);
        }
      }
    });
    highlightedProperty().addListener((obs, oldVal, newVal) -> {
      if (selected.get()) {
        hex.setStrokeWidth(4.5);
        label.setStyle(LabelFactory.troopLabelStyle(Color.IMPERIAL_GOLD, Color.VOLCANIC_BLACK));
      } else {
        label.setStyle(LabelFactory.troopLabelStyle(Color.VOLCANIC_BLACK, Color.IVORY_WHITE));
        if (newVal) {
          hex.setStrokeWidth(3.0);
        } else {
          hex.setStrokeWidth(1.0);
        }
      }
    });
  }

  private void fillBackground(Player owner, boolean selected, boolean highlighted) {
    var newColor = Optional.ofNullable(owner)
        .map(Player::getColor)
        .map(color -> UIUtil.alpha(color, selected ? 1.0 : (highlighted ? 0.6 : 0.2)))
        .orElseGet(Color.IVORY_WHITE::get);
    hex.setFill(newColor);
  }

  private void renderToolTip() {
    Tooltip tooltip = new Tooltip();
    tooltip.textProperty().bind(Bindings.createStringBinding(
        () -> {
          Player owner = territory.getOwner();
          return String.format(
              "Territory: %s\nRegion: %s\n%s",
              territory.getName(),
              territory.getRegion().getName(),
              owner == null ? "" : String.format("Regent: %s", owner.getUsername())
          );
        },
        owner
    ));
    tooltip.setShowDelay(Duration.millis(50));
    tooltip.setHideDelay(Duration.millis(100));
    Tooltip.install(this, tooltip);
  }

  private void setMouseEvent() {
    hex.setOnMouseEntered(e -> {
      if (!highlighted.get() && !selected.get()) {
        hex.setStrokeWidth(3.0);
      }
      setCursor(Cursor.HAND);
    });
    hex.setOnMouseExited(e -> {
      if (!highlighted.get() && !selected.get()) {
        hex.setStrokeWidth(1.0);
      }
      setCursor(Cursor.DEFAULT);
    });
  }

  public SimpleBooleanProperty selectedProperty() {
    return selected;
  }

  public SimpleBooleanProperty highlightedProperty() {
    return highlighted;
  }

  public ObjectProperty<Player> ownerProperty() {
    return owner;
  }

  public SimpleIntegerProperty numberOfTroopsProperty() {
    return numberOfTroops;
  }

  public DoubleProperty strokeWidthProperty() {
    return hex.strokeWidthProperty();
  }

  public ObjectProperty<Paint> fillProperty() {
    return hex.fillProperty();
  }

  public Territory getTerritory() {
    return territory;
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
