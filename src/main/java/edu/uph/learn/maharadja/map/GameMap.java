package edu.uph.learn.maharadja.map;

import javafx.geometry.Point2D;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class GameMap {
  /**
   * Lamuri                Water               Water
   *  (0,5)                (1,5)               (2,5)
   *        \          /            \           /
   *          [Barus]   --------  Toba Highlands
   *           (1,6)                  (2,6)
   *           /        \           /           \
   *     Water  ----- [Minangkabau]    ---------   Indrapura
   *      (0,7)           (1,7)                     (2,7)
   *         \         /           \             /           \
   *           Water    ----------   Bengkulu      ---------   Jambi
   *           (1,8)                  (2,8)                    (3,8)
   */
  public static final int[][][] AXIAL_NEIGHBORS = new int[][][] {
      //             East,   North West,  North East,   West,      South West,  South East
      new int[][] { {1, 0},   {-1, -1},    {0, -1},    {-1, 0},     {-1, 1},     {0, 1} },      // Even Row
      new int[][] { {1, 0},   {0, -1},     {1, -1},    {-1, 0},      {0, 1},     {1, 1} },      // Odd Row
  };
  private static final Logger log = LoggerFactory.getLogger(GameMap.class);

  final String tile;
  final int q;
  final int r;
  final Map<Point2D, Territory> territoryMap;
  final List<Region> regionList;
  final List<Territory> territoryList;
  final Map<Territory, Set<Territory>> adjacencyList;
  final Set<Pair<Territory, Territory>> waterConnections;

  public GameMap() {
    this(0, 0, "HEX");
  }

  public GameMap(int q, int r, String tile) {
    this.q = q;
    this.r = r;
    this.tile = tile;
    this.territoryMap = new HashMap<>();
    this.regionList = new ArrayList<>();
    this.territoryList = new ArrayList<>();
    this.adjacencyList = new HashMap<>();
    this.waterConnections = new HashSet<>();
  }

  //region should only be used by GameMapLoader
  void addRegion(Region region) {
    if (regionList.contains(region)) {
      return;
    }
    regionList.add(region);
  }

  void addTerritory(Territory territory) {
    if (adjacencyList.containsKey(territory)) {
      return;
    }

    territoryMap.put(territory.getPoint(), territory);
    territoryList.add(territory);
    adjacencyList.putIfAbsent(territory, new HashSet<>());

    // compute adjacency, since we store it bidirectional, if a new one attached to a previously added old one,
    // then the old one "neighbor" will be discovered automatically
    if ("HEX".equals(tile)) {
      for (int[] direction : AXIAL_NEIGHBORS[territory.getR() % 2]) {
        Point2D coordinate = new Point2D(territory.getQ() + direction[0], territory.getR() + direction[1]);
        Optional.ofNullable(territoryMap.get(coordinate))
            .filter(neighbor -> !neighbor.equals(territory))
            .ifPresent(neighbor -> addConnection(territory, neighbor));
      }
    }
  }

  void addConnection(Territory origin, Territory destination) {
    addTerritory(origin);
    addTerritory(destination);
    adjacencyList.get(origin).add(destination);
    adjacencyList.get(destination).add(origin);
    if ("HEX".equals(tile) && !isDirectNeighbors(origin, destination)) {
      if (!waterConnections.contains(new Pair<>(destination, origin))) {
        waterConnections.add(new Pair<>(origin, destination));
      }
    }
  }

  boolean isDirectNeighbors(Territory origin, Territory destination) {
    int[][] directions = AXIAL_NEIGHBORS[origin.getR() % 2];
    for (int[] direction : directions) {
      if (origin.getQ() + direction[0] == destination.getQ() && origin.getR() + direction[1] == destination.getR()) {
        return true;
      }
    }
    return false;
  }
  //endregion

  public int getQ() {
    return q;
  }

  public int getR() {
    return r;
  }

  public String getTile() {
    return tile;
  }

  public Set<Pair<Territory, Territory>> getWaterConnections() {
    return Set.copyOf(waterConnections);
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
    return List.copyOf(region.getTerritories());
  }

  public boolean isAttackable(Territory origin, Territory destination) {
    return !Objects.equals(origin.getOwner(), destination.getOwner())
        && adjacencyList.get(origin).contains(destination);
  }

  public Set<Territory> getAdjacentTerritories(Territory origin) {
    return adjacencyList.get(origin);
  }

  /**
   * <p>
   * Helper to check if two territories are connected by the same owner.
   * Return the list of territory
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
    // If it isn't then, there's no path to the territory.
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
        if (visited.contains(next)) {
          continue;
        }
        if (!Objects.equals(next.getOwner(), current.getOwner())) {
          continue;
        }
        visited.add(next);
        queue.add(next);
        parentMap.put(next, current);
      }
    }

    return List.of();
  }

  /**
   * Use BFS to traverse the tile and find the shortest water path.
   */
  public List<Point2D> getShortestNavalPath(Territory origin, Territory destination) {
    // The territory is not even adjacent past water
    if (!adjacencyList.get(origin).contains(destination)) {
      log.info("Missing connection between {} to {}", origin.getName(), destination.getName());
      return List.of();
    }

    Queue<Point2D> queue = new LinkedList<>();
    Set<Point2D> visited = new HashSet<>();
    Map<Point2D, Point2D> parentMap = new HashMap<>(); // for backtracking

    queue.add(origin.getPoint());
    visited.add(origin.getPoint());

    while (!queue.isEmpty()) {
      Point2D current = queue.poll();
      if (current.equals(destination.getPoint())) {
        List<Point2D> path = new ArrayList<>();
        for (Point2D at = destination.getPoint(); at != null; at = parentMap.get(at)) {
          path.add(at);
        }
        Collections.reverse(path);
        return Collections.unmodifiableList(path);
      }

      int[][] directions = AXIAL_NEIGHBORS[(int) (current.getY() % 2)];
      for (int[] direction : directions) {
        Point2D next = new Point2D(((int) current.getX() )+ direction[0], ((int) current.getY()) + direction[1]);
        if (next.getX() < 0 || next.getY() < 0 || next.getX() > q || next.getY() > r) continue; // bound check
        if (visited.contains(next)) continue;
        if (territoryMap.get(next) != null
            && !next.equals(origin.getPoint())
            && !next.equals(destination.getPoint())
        ) continue;
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
