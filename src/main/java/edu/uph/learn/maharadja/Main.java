package edu.uph.learn.maharadja;

import edu.uph.learn.maharadja.network.GameClient;
import edu.uph.learn.maharadja.network.GameServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  private static GameServer server = null;
  private static GameClient client = null;

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
      // TODO: run as UI mode
    }
  }
}
