package edu.uph.learn.maharadja.map;

import edu.uph.learn.maharadja.common.Color;

import java.util.Objects;

public class Region {
  private final String name;
  private final int bonusTroops;
  private Integer owner;
  private Color color;

  public Region(String name) {
    this.name = name;
    this.bonusTroops = 0;
    this.color = Color.IMPERIAL_GOLD;
  }

  public Region(String name, int bonusTroops, Color color) {
    this.name = name;
    this.bonusTroops = bonusTroops;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public int getBonusTroops() {
    return bonusTroops;
  }

  public Color getColor() {
    return color;
  }

  public Integer getOwner() {
    return owner;
  }

  public void setOwner(Integer owner) {
    this.owner = owner;
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Region region)) {
      return false;
    }

    return Objects.equals(name, region.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }
}
