package edu.uph.learn.maharadja.ui.scene;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.common.UI;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.event.RegionForfeitedEvent;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.game.event.RegionOccupiedEvent;
import edu.uph.learn.maharadja.game.event.TerritoryOccupiedEvent;
import edu.uph.learn.maharadja.map.GameMap;
import edu.uph.learn.maharadja.map.GameMapLoader;
import edu.uph.learn.maharadja.map.MapType;
import edu.uph.learn.maharadja.ui.GameWindow;
import edu.uph.learn.maharadja.ui.components.MapPane;
import edu.uph.learn.maharadja.ui.components.SidePane;
import edu.uph.learn.maharadja.ui.state.DialogState;
import edu.uph.learn.maharadja.ui.state.DialogTask;
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

    GameMap gameMap = GameMapLoader.load(MapType.HEX);
    ((HBox) getRoot()).getChildren().addAll(
        new MapPane(gameMap),
        new SidePane()
    );

    // ensure GameEngine is initialized last after all components register the listener to EventBus
    this.gameEngine = GameEngine.init(gameMap);
    gameEngine.setOnGameResult(gameWindow::openEndScene);

    EventBus.registerListener(TerritoryOccupiedEvent.class, event -> DialogState.get().enqueue(new DialogTask(
        String.format("Congratulations!\n%s occupied the territory %s!",
            event.player().getUsername(),
            event.territory().getName()
        )
    )));
    EventBus.registerListener(RegionOccupiedEvent.class, event -> DialogState.get().enqueue(new DialogTask(
        String.format("Congratulations!\n%s occupied the region %s, you will gain %d bonus troops if you maintained position!",
            event.player().getUsername(),
            event.region().getName(),
            event.region().getBonusTroops()
        )
    )));
    EventBus.registerListener(RegionForfeitedEvent.class, event -> DialogState.get().enqueue(new DialogTask(
        String.format("Too bad!\n%s took over a territory from region %s, previously won by %s!",
            event.attacker().getUsername(),
            event.region().getName(),
            event.previousRegent().getUsername()
        )
    )));
  }
}
