package edu.uph.learn.maharadja.ui;

import edu.uph.learn.maharadja.event.BotActionEvent;
import edu.uph.learn.maharadja.event.EventBus;
import edu.uph.learn.maharadja.game.GameEngine;
import edu.uph.learn.maharadja.game.GameResult;
import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.map.Territory;
import edu.uph.learn.maharadja.player.Player;
import edu.uph.learn.maharadja.ui.event.CombatResultEvent;
import edu.uph.learn.maharadja.ui.scene.DefeatScene;
import edu.uph.learn.maharadja.ui.scene.VictoryScene;
import edu.uph.learn.maharadja.ui.scene.GameScene;
import edu.uph.learn.maharadja.ui.scene.LobbyScene;
import edu.uph.learn.maharadja.ui.state.DialogState;
import edu.uph.learn.maharadja.ui.state.LoadingState;
import edu.uph.learn.maharadja.ui.state.TileSelectionState;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Main class that manage the transition between JavaFX scene.
 */
public class GameWindow {
  private static final Logger LOG = LoggerFactory.getLogger(GameWindow.class);

  private final Stage stage;
  private final DialogState dialogState;
  private final LoadingState loadingState;
  private final TileSelectionState tileSelectionState;

  public GameWindow(Stage stage) {
    this.stage = stage;
    stage.setTitle("Maharadja");
    stage.getIcons().add(new Image("assets/icon.png"));
    stage.setResizable(false);
    openLobby();

    dialogState = DialogState.init(stage);
    EventBus.registerListener(BotActionEvent.class, dialogState::onBotActionEvent);
    EventBus.registerListener(CombatResultEvent.class, dialogState::onCombatResultEvent);
    loadingState = LoadingState.get();
    tileSelectionState = TileSelectionState.get();

    //// Debugging
    //// -- skip lobby
    registerPlayer("GajahMada", true);
    //// -- auto draft
    //Player currentPlayer = GameState.get().currentTurn();
    //Territory territory = currentPlayer.getTerritories().iterator().next();
    //int numOfTroops = Math.max(3, Math.floorDiv(currentPlayer.getTerritories().size(),  3));
    //GameEngine.get().draftTroop(Map.of(territory, numOfTroops));
    //// -- skip attack
    //GameEngine.get().nextPhase();
    //// -- skip fortify
    //GameEngine.get().nextPhase();
    ////// -- go to final state
    ////openEndScene(new GameResult(new Player("GajahMada"), false));
  }

  public void openLobby() {
    LOG.info("Opening Lobby...");
    stage.setScene(new LobbyScene(this));
    stage.show();
  }

  public void registerPlayer(final String username, boolean isServer) {
    LOG.info("Registering [{}] as [{}]...", username, isServer ? "HOST" : "GUEST");
    Player player = new Player(username);
    GameState.get().registerPlayer(player, true);
    GameState.get().start();

    // TODO: transition to server/client scene
    LOG.info("Loading the game up...");
    stage.setScene(new GameScene(this));
    stage.show();
  }

  public void openEndScene(GameResult gameResult) {
    if (gameResult.victory()) {
      stage.setScene(new VictoryScene(this));
    } else {
      stage.setScene(new DefeatScene(this));
    }
    stage.show();
  }
}
