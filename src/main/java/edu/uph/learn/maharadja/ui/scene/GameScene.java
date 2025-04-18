package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.Constant;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.ui.GameWindow;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GameScene extends Scene {
  private static final double TAB_WIDTH = 320;
  private final GameWindow gameWindow;

  public GameScene(GameWindow gameWindow) {
    super(new HBox(), Constant.DEFAULT_WIDTH, Constant.DEFAULT_HEIGHT);
    this.gameWindow = gameWindow;
    setFill(Color.IVORY_WHITE.get());

    HBox root = (HBox) getRoot();

    ScrollPane left = new ScrollPane();
    left.setBackground(Background.fill(Color.IMPERIAL_GOLD.get()));
    HBox.setHgrow(left, Priority.ALWAYS);

    VBox right = new VBox();
    right.setBackground(Background.fill(Color.VOLCANIC_BLACK.get()));
    right.setPrefWidth(TAB_WIDTH);
    right.setMinWidth(TAB_WIDTH);
    right.setMaxWidth(TAB_WIDTH);

    TabPane tabPane = new TabPane();
    tabPane.setSide(Side.RIGHT);
    tabPane.setMinWidth(TAB_WIDTH);
    tabPane.setMaxWidth(TAB_WIDTH);
    tabPane.setPrefWidth(TAB_WIDTH);
    VBox.setVgrow(tabPane, Priority.ALWAYS);

    for (Player player : GameState.getPlayerList()) {
      Tab tab = new Tab(player.getUsername(), new HBox());
      tabPane.getTabs().add(tab);
    }

    right.getChildren().add(tabPane);


    root.getChildren().addAll(left, right);
  }
}
