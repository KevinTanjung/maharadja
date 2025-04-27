package edu.uph.learn.maharadja.player;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.map.Territory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Player {
  public static final List<Color> PLAYER_COLORS = List.of(
      Color.DEEP_MAROON,
      Color.JADE_GREEN,
      Color.ROYAL_INDIGO,
      Color.COCONUT_HUSK
  );

  private final String sessionId;
  private final String ipAddress;
  private final String username;
  protected boolean computer;
  private Color color;
  private final Set<Territory> territories = new HashSet<>();
  private boolean forfeited;

  public Player(String username) {
    sessionId = UUID.randomUUID().toString();
    ipAddress = "127.0.0.1";
    this.username = username;
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

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Set<Territory> getTerritories() {
    return Collections.unmodifiableSet(territories);
  }

  public void addTerritory(Territory territory) {
    territories.add(territory);
  }

  public void removeTerritory(Territory territory) {
    territories.remove(territory);
  }

  public boolean isForfeited() {
    return forfeited;
  }

  public void setForfeited(boolean forfeited) {
    this.forfeited = forfeited;
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Player player)) {
      return false;
    }

    return Objects.equals(sessionId, player.sessionId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(sessionId);
  }

  @Override
  public String toString() {
    return "Player{" +
        "username='" + username + '\'' +
        '}';
  }
}
