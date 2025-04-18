package edu.uph.learn.maharadja.ui;

import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.game.Player;
import edu.uph.learn.maharadja.ui.scene.GameScene;
import edu.uph.learn.maharadja.ui.scene.LobbyScene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GameWindow {
  private final Stage stage;

  public GameWindow(Stage stage) {
    this.stage = stage;
    stage.setTitle("Maharadja");
    stage.getIcons().add(new Image("assets/icon.png"));
    stage.setResizable(false);
    openLobby();
  }

  public void openLobby() {
    stage.setScene(new LobbyScene(this));
    stage.show();
  }

  public void registerPlayer(
      final String username,
      boolean isServer
  ) {
    Player player = Player.user(username);
    GameState.get().registerPlayer(player, true);
    GameState.get().start();

    // TODO: transition to server/client scene
    stage.setScene(new GameScene(this));
    stage.show();
  }
}
