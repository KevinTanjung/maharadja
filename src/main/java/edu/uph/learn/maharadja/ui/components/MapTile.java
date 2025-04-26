package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.ui.TileType;
import edu.uph.learn.maharadja.ui.event.TerritorySelectedEvent;
import edu.uph.learn.maharadja.ui.factory.LabelFactory;
import edu.uph.learn.maharadja.utils.UIUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.util.Optional;

import static edu.uph.learn.maharadja.common.UI.HEX_SIZE;

/**
 * Inspired by <a href="https://www.redblobgames.com/grids/hexagons-v1">source</a>
 */
public class MapTile extends Group {
  private final ObjectProperty<Player> owner = new SimpleObjectProperty<>();
  private final SimpleIntegerProperty numberOfTroops = new SimpleIntegerProperty();
  private final SimpleBooleanProperty highlighted = new SimpleBooleanProperty();
  private final Territory territory;
  private final int q;
  private final int r;
  private final TileType tileType;
  private final Label label;
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

    label = LabelFactory.troopLabel();
    getChildren().addAll(hex, label);

    if (territory == null) {
      hex.setStroke(UIUtil.alpha(Color.SKY_BLUE, 0.3));
      hex.setFill(Color.ALICE_BLUE.get());
      return;
    }

    hex.setStroke(territory.getRegion().getColor().get());
    hex.fillProperty().bind(Bindings.createObjectBinding(
        () -> Optional.ofNullable(owner.get())
            .map(Player::getColor)
            .map(color -> UIUtil.alpha(color, highlighted.get() ? 1.0 : 0.5))
            .orElseGet(Color.IVORY_WHITE::get),
        owner,
        highlighted
    ));
    setMouseEvent();
    renderToolTip();
    label.textProperty().bind(numberOfTroops.asString());
    label.setVisible(true);
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
    setOnMouseEntered(e -> {
      hex.setStrokeWidth(2);
      setCursor(Cursor.HAND);
    });
    setOnMouseExited(e -> {
      hex.setStrokeWidth(1);
      setCursor(Cursor.DEFAULT);
    });
    setOnMouseClicked(e -> {
      EventBus.emit(new TerritorySelectedEvent(this, territory, null));
    });
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
