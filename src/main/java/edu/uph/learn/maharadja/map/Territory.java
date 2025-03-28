package edu.uph.learn.maharadja.map;

public class Territory {
  private final Region region;
  private final String name;
  private int owner;
  private int numberOfStationedTroops;

  public Territory(Region region, String name) {
    this.region = region;
    this.name = name;
    this.numberOfStationedTroops = 1;
  }

  public Region getRegion() {
    return region;
  }

  public String getName() {
    return name;
  }

  public int getOwner() {
    return owner;
  }

  public void setOwner(int owner) {
    this.owner = owner;
  }

  public int getNumberOfStationedTroops() {
    return numberOfStationedTroops;
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
}
