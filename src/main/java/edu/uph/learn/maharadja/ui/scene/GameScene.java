package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.GameMapLoader;
import edu.uph.learn.maharadja.map.MapType;
import edu.uph.learn.maharadja.ui.GameWindow;
import edu.uph.learn.maharadja.ui.components.MapPane;
import edu.uph.learn.maharadja.ui.components.SidePane;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GameScene extends Scene {
  private final GameWindow gameWindow;

  public GameScene(GameWindow gameWindow) {
    super(new HBox(), Constant.DEFAULT_WIDTH, Constant.DEFAULT_HEIGHT);
    this.gameWindow = gameWindow;
    setFill(Color.IVORY_WHITE.get());

    GameMap gameMap = GameMapLoader.load(MapType.CLASSIC);
    GameState.setGameMap(gameMap);

    HBox root = (HBox) getRoot();
    ScrollPane left = new MapPane(gameMap);
    HBox.setHgrow(left, Priority.ALWAYS);
    VBox right = new SidePane();
    root.getChildren().addAll(left, right);
  }
}
