package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
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
    super(new HBox(), UI.WIDTH, UI.HEIGHT);
    this.gameWindow = gameWindow;
    setFill(Color.IVORY_WHITE.get());

    GameMap gameMap = GameMapLoader.load(MapType.CLASSIC);
    ((HBox) getRoot()).getChildren().addAll(
        new MapPane(gameMap),
        new SidePane()
    );

    // ensure GameEngine is initialized last after all components register the listener to EventBus
    this.gameEngine = GameEngine.init(gameMap);
    gameEngine.setOnGameResult(gameWindow::openEndScene);
  }
}
