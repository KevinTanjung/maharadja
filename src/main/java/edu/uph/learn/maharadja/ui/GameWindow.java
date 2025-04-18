package edu.uph.learn.maharadja.ui;

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
    stage.setScene(new LobbyScene());
    stage.show();
  }
}
