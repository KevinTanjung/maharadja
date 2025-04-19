package edu.uph.learn.maharadja.ui.components;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.map.Region;
import edu.uph.learn.maharadja.map.Territory;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Polygon;

import java.util.List;
import java.util.Random;

import static edu.uph.learn.maharadja.common.UI.HEX_SIZE;

/**
 * Inspired by <a href="https://www.redblobgames.com/grids/hexagons-v1">source</a>
 */
public class MapTile extends Polygon {
  private static final Random RANDOM = new Random();
  private static final List<javafx.scene.paint.Color> WATER_COLORS = List.of(
      Color.ALICE_BLUE.get(),
      javafx.scene.paint.Color.web(Color.SKY_BLUE.toHex(), 0.3),
      javafx.scene.paint.Color.web(Color.LIGHT_BLUE.toHex(), 0.1)
  );

  private final Territory territory;
  private final Region region;

  public MapTile(int q, int r) {
    this(null, null, q, r);
  }

  public MapTile(Territory territory) {
    this(territory, territory.getRegion(), territory.getQ(), territory.getR());
  }

  private MapTile(Territory territory, Region region, int q, int r) {
    super();
    this.territory = territory;
    this.region = region;

    for (int i = 0; i < 6; i++) {
      double angle = Math.toRadians(60 * i + 30);
      getPoints().addAll(HEX_SIZE * Math.cos(angle), HEX_SIZE * Math.sin(angle));
    }

    Tooltip tooltip = new Tooltip();
    if (region == null) {
      var color = getWaterColor();
      setStroke(Color.SKY_BLUE.get());
      setFill(color);
      tooltip.setText("Water " + color.toString());
    } else {
      setStroke(region.getColor() != null ? region.getColor().get() : Color.VOLCANIC_BLACK.get());
      setFill(Color.IVORY_WHITE.get());
      setOnMouseEntered(e -> setStrokeWidth(2));
      setOnMouseExited(e -> setStrokeWidth(1));
      tooltip.setText("Territory: " + territory.getName() + "\n" + "Region: " + region.getName());
    }
    Tooltip.install(this, tooltip);

    double x = HEX_SIZE * Math.sqrt(3) * (q + 0.5 * (r & 1));
    double y = HEX_SIZE * 3 / 2 * r;
    setTranslateX(x);
    setTranslateY(y);
  }

  private javafx.scene.paint.Color getWaterColor() {
    return WATER_COLORS.get(RANDOM.nextInt(3));
  }
}
