package edu.uph.learn.maharadja.ui;

import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.ui.scene.GameScene;
import edu.uph.learn.maharadja.ui.scene.LobbyScene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class that manage the transition between JavaFX scene.
 */
public class GameWindow {
  private static final Logger LOG = LoggerFactory.getLogger(GameWindow.class);

  private final Stage stage;

  public GameWindow(Stage stage) {
    this.stage = stage;
    stage.setTitle("Maharadja");
    stage.getIcons().add(new Image("assets/icon.png"));
    stage.setResizable(false);
    openLobby();

    // Debugging
    registerPlayer("GajahMada", true);
  }

  public void openLobby() {
    LOG.info("Opening Lobby...");
    stage.setScene(new LobbyScene(this));
    stage.show();
  }

  public void registerPlayer(
      final String username,
      boolean isServer
  ) {
    LOG.info("Registering [{}] as [{}]...", username, isServer ? "HOST" : "GUEST");
    Player player = Player.user(username);
    GameState.get().registerPlayer(player, true);
    GameState.get().start();

    // TODO: transition to server/client scene
    LOG.info("Loading the game up...");
    stage.setScene(new GameScene(this));
    stage.show();
  }
}
