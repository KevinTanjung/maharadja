package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.GameMapLoader;
import edu.uph.learn.maharadja.map.MapType;
import edu.uph.learn.maharadja.ui.GameWindow;
import edu.uph.learn.maharadja.ui.components.MapPane;
import edu.uph.learn.maharadja.ui.components.SidePane;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

@SuppressWarnings( {"FieldCanBeLocal", "unused"})
public class GameScene extends Scene {
  private final GameWindow gameWindow;
  private final GameEngine gameEngine;

  public GameScene(GameWindow gameWindow) {
    super(new HBox(), Constant.DEFAULT_WIDTH, Constant.DEFAULT_HEIGHT);
    this.gameWindow = gameWindow;
    setFill(Color.IVORY_WHITE.get());

    GameMap gameMap = GameMapLoader.load(MapType.CLASSIC);
    this.gameEngine = new GameEngine(gameMap);

    ((HBox) getRoot()).getChildren().addAll(
        new MapPane(gameMap),
        new SidePane()
    );
  }
}
