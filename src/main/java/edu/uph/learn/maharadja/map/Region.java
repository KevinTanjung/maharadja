package edu.uph.learn.maharadja.map;

import edu.uph.learn.maharadja.common.Color;
import edu.uph.learn.maharadja.game.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Region implements Comparable<Region> {
  private final String name;
  private final int bonusTroops;
  private final Color color;
  private final Set<Territory> territories = new HashSet<>();
  private Player owner;

  public Region(String name) {
    this(name, 0, Color.IMPERIAL_GOLD);
  }

  public Region(String name, int bonusTroops, Color color) {
    this.name = name;
    this.bonusTroops = bonusTroops;
    this.color = color;
  }

  /**
   * Set to package-private, since only {@link Territory} should access this.
   */
  void addRegion(Territory territory) {
    territories.add(territory);
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

  public Set<Territory> getTerritories() {
    return Collections.unmodifiableSet(territories);
  }

  public Player getOwner() {
    return owner;
  }

  public void setOwner(Player owner) {
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

  @Override
  public int compareTo(Region o) {
    return getName().compareTo(o.getName());
  }
}
