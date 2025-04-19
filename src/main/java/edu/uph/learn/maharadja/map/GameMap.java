package edu.uph.learn.maharadja.map;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class GameMap {
  final int q;
  final int r;
  final Map<Point2D, Territory> territoryMap;
  final List<Region> regionList;
  final List<Territory> territoryList;
  final Map<Region, Set<Territory>> regionToTerritoryMap;
  final Map<Territory, Set<Territory>> adjacencyList;

  public GameMap() {
    this(0, 0);
  }

  public GameMap(int q, int r) {
    this.q = q;
    this.r = r;
    this.territoryMap = new HashMap<>();
    this.regionList = new ArrayList<>();
    this.territoryList = new ArrayList<>();
    this.regionToTerritoryMap = new HashMap<>();
    this.adjacencyList = new HashMap<>();
  }

  //region should only be used by GameMapLoader
  void addRegion(Region region) {
    if (regionToTerritoryMap.containsKey(region)) {
      return;
    }

    regionList.add(region);
    regionToTerritoryMap.putIfAbsent(region, new HashSet<>());
  }

  void addTerritory(Territory territory) {
    if (adjacencyList.containsKey(territory)) {
      return;
    }

    territoryMap.put(new Point2D(territory.getQ(), territory.getR()), territory);
    territoryList.add(territory);
    regionToTerritoryMap.putIfAbsent(territory.getRegion(), new HashSet<>());
    regionToTerritoryMap.get(territory.getRegion()).add(territory);
    adjacencyList.putIfAbsent(territory, new HashSet<>());
  }

  void addConnection(Territory origin, Territory destination) {
    addTerritory(origin);
    addTerritory(destination);
    adjacencyList.get(origin).add(destination);
    adjacencyList.get(destination).add(origin);
  }
  //endregion

  public int getQ() {
    return q;
  }

  public int getR() {
    return r;
  }

  public Map<Point2D, Territory> getTerritoryMap() {
    return territoryMap;
  }

  public List<Region> getAllRegions() {
    return regionList;
  }

  public List<Territory> getAllTerritories() {
    return territoryList;
  }

  public List<Territory> getTerritoriesByRegion(Region region) {
    return regionToTerritoryMap.getOrDefault(region, Set.of()).stream().toList();
  }

  public boolean isAttackable(Territory origin, Territory destination) {
    return !Objects.equals(origin.getOwner(), destination.getOwner())
        && adjacencyList.get(origin).contains(destination);
  }

  /**
   * <p>
   *   Helper to check if two territories are connected by the same owner.
   *   Return the list of territory
   * </p>
   */
  public List<Territory> getShortestDeploymentPath(
      Territory origin,
      Territory destination
  ) {
    // The territory is not even in the list.
    if (!adjacencyList.containsKey(origin) || !adjacencyList.containsKey(destination)) {
      return List.of();
    }
    // Ensure that the origin and destination are owned by the same person.
    // If it isn't then, there's no path to the destination.
    if (!Objects.equals(origin.getOwner(), destination.getOwner())) {
      return List.of();
    }

    Queue<Territory> queue = new LinkedList<>();
    Map<Territory, Territory> parentMap = new HashMap<>();
    Set<Territory> visited = new HashSet<>();

    queue.add(origin);
    visited.add(origin);
    parentMap.put(origin, null);

    while (!queue.isEmpty()) {
      Territory current = queue.poll();
      if (current.equals(destination)) {
        return reconstructPath(parentMap, destination);
      }

      for (Territory next : adjacencyList.get(current)) {
        if (visited.contains(next)) continue;
        if (!Objects.equals(next.getOwner(), current.getOwner())) continue;
        visited.add(next);
        queue.add(next);
        parentMap.put(next, current);
      }
    }

    return List.of();
  }

  private List<Territory> reconstructPath(
      Map<Territory, Territory> parentMap,
      Territory destination
  ) {
    List<Territory> path = new ArrayList<>();
    for (Territory at = destination; at != null; at = parentMap.get(at)) {
      path.add(at);
    }
    Collections.reverse(path);
    return Collections.unmodifiableList(path);
  }
}
