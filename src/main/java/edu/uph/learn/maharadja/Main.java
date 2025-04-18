package edu.uph.learn.maharadja;

import edu.uph.learn.maharadja.game.GameState;
import edu.uph.learn.maharadja.network.GameClient;
import edu.uph.learn.maharadja.network.GameServer;
import edu.uph.learn.maharadja.ui.GameWindow;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings( {"FieldCanBeLocal", "unused"})
@Slf4j
public class Main extends Application {
  private static GameServer server;
  private static GameClient client;
  private static GameState state;
  private static GameWindow window;

  public static void main(String[] args) {
    if (args.length == 2 && args[0].equals("--server")) {
      String username = args[1];
      log.info("Running as server in debug mode as: {}", username);
      server = GameServer.start(username);
    } else if (args.length == 2 && args[0].equals("--client")) {
      String username = args[1];
      log.info("Running as client in debug mode as: {}", username);
      client = GameClient.start(username);
    } else {
      log.info("Launching Maharadja UI...");
      launch(args);
    }
  }

  @Override
  public void start(Stage primaryStage) {
    state = GameState.init();
    window = new GameWindow(primaryStage);
  }
}
