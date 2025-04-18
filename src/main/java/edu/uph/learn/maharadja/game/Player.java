package edu.uph.learn.maharadja.game;

import java.util.UUID;

public class Player {
  private String sessionId;
  private String ipAddress;
  private String username;
  private boolean computer;

  public static Player user(String username) {
    Player player = new Player();
    player.sessionId = UUID.randomUUID().toString();
    player.ipAddress = "127.0.0.1";
    player.username = username;
    return player;
  }

  public static Player computer(int i) {
    Player player = Player.user("Computer " + i);
    player.computer = true;
    return player;
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getUsername() {
    return username;
  }

  public boolean isComputer() {
    return computer;
  }
}
