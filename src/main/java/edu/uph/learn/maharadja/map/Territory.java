package edu.uph.learn.maharadja.map;

import java.util.Objects;

public class Territory {
  private final Region region;
  private final String name;
  private Integer owner;
  private int numberOfStationedTroops;
  private final int q;
  private final int r;

  public Territory(Region region, String name) {
    this.region = region;
    this.name = name;
    this.numberOfStationedTroops = 1;
    this.q = 0;
    this.r = 0;
  }

  public Territory(Region region, String name, int q, int r) {
    this.region = region;
    this.name = name;
    this.q = q;
    this.r = r;
  }

  public Region getRegion() {
    return region;
  }

  public String getName() {
    return name;
  }

  public Integer getOwner() {
    return owner;
  }

  public void setOwner(int owner) {
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
    numberOfStationedTroops += count;
  }

  public void withdrawTroop(int count) {
    if (numberOfStationedTroops - count < 1) {
      return;
    }
    numberOfStationedTroops -= count;
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
  public int hashCode() {
    int result = Objects.hashCode(region);
    result = 31 * result + Objects.hashCode(name);
    return result;
  }
}
