package edu.uph.learn.maharadja;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    if (args.length == 2 && args[0].equals("--server")) {
      // TODO: run as server mode
      LOG.info("Running as server in debug mode as: {}", args[1]);
    } else if (args.length == 2 && args[0].equals("--client")) {
      // TODO: run as client mode
      LOG.info("Running as client in debug mode as: {}", args[1]);
    } else {
      LOG.info("Launching Maharadja UI...");
      // TODO: run as UI mode
    }
  }
}
