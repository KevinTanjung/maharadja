package edu.uph.learn.maharadja;

public class Main {
  public static void main(String[] args) {
    if (args.length == 2 && args[0].equals("--server")) {
      // TODO: run as server mode
      System.out.println("Running as server in debug mode as: " + args[1]);
    } else if (args.length == 2 && args[0].equals("--client")) {
      // TODO: run as client mode
      System.out.println("Running as client in debug mode as: " + args[1]);
    } else {
      System.out.println("Launching Maharadja UI...");
      // TODO: run as UI mode
    }
  }
}
