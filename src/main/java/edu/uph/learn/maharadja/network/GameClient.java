package edu.uph.learn.maharadja.network;

import edu.uph.learn.maharadja.Constant;
import edu.uph.learn.maharadja.controller.common.Action;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class GameClient {
  private static boolean started;
  private static boolean connected;
  private static String username;
  private static Socket serverSocket;
  private static PrintWriter out;

  //region Singleton
  private GameClient() {
  }

  private static class GameClientHolder {
    private static volatile GameClient INSTANCE = new GameClient();
  }

  public static GameClient start(String username) {
    if (GameClient.started) {
      return GameClientHolder.INSTANCE;
    }

    GameClient.started = true;
    GameClient.username = username;
    String serverIp = "127.0.0.1";
    try {
      serverSocket = new Socket(serverIp, GameServer.DEFAULT_PORT);
      out = new PrintWriter(serverSocket.getOutputStream(), true);
      out.println(Action.REGISTER + "||" + GameClient.username);
      new Thread(GameClientHolder.INSTANCE::listenForMessages).start();
    } catch (IOException e) {
      log.error("[start] Failed to connect.");
      recreate();
    }

    return GameClientHolder.INSTANCE;
  }

  private static void recreate() {
    synchronized (GameClientHolder.class) {
      GameClient.started = false;
      GameClientHolder.INSTANCE = new GameClient();
    }
  }
  //endregion

  private void listenForMessages() {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))) {
      String rawMessage;
      while ((rawMessage = in.readLine()) != null) {
        if (Constant.LOBBY_FULL.equals(rawMessage)) {
          GameClient.recreate();
          break;
        }
        log.info("Game Update: {}", rawMessage);
      }
    } catch (IOException e) {
      log.error("[listenForMessages] Exception!", e);
    }
  }
}
