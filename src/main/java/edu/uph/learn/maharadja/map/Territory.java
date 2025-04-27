package edu.uph.learn.maharadja.map;

import edu.uph.learn.maharadja.player.Player;
import javafx.geometry.Point2D;

import java.util.Objects;

public class Territory {
  private final Region region;
  private final String name;
  private Player owner;
  private int numberOfStationedTroops;
  private final int q;
  private final int r;

  public Territory(Region region, String name) {
    this(region, name, 0, 0);
  }

  public Territory(Region region, String name, int q, int r) {
    region.addRegion(this);
    this.region = region;
    this.name = name;
    this.numberOfStationedTroops = 1;
    this.q = q;
    this.r = r;
  }

  public Region getRegion() {
    return region;
  }

  public String getName() {
    return name;
  }

  public Player getOwner() {
    return owner;
  }

  public Point2D getPoint() {
    return new Point2D(q, r);
  }

  public void setOwner(Player owner) {
    this.owner = owner;
  }

  public int getNumberOfStationedTroops() {
    return numberOfStationedTroops;
  }

  public int getQ() {
    return q;
  }

  public int getR() {
    return r;
  }

  public void deployTroop(int count) {
    if (count > 0) {
      numberOfStationedTroops += count;
    }
  }

  public void withdrawTroop(int count) {
    if (count >= 0) {
      numberOfStationedTroops -= count;
    }
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Territory territory)) {
      return false;
    }

    return Objects.equals(region, territory.region) && Objects.equals(name, territory.name);
  }

  @Override
  public String toString() {
    return "Territory{" +
        "region=" + region.getName() +
        ", name='" + name + '\'' +
        ", owner=" + (owner == null ? "null" : owner.getUsername()) +
        ", numberOfStationedTroops=" + numberOfStationedTroops +
        ", q=" + q +
        ", r=" + r +
        '}';
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(region);
    result = 31 * result + Objects.hashCode(name);
    return result;
  }
}
