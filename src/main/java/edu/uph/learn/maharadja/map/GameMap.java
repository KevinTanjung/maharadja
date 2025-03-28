package edu.uph.learn.maharadja.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GameMap {
  Map<Territory, Set<Territory>> adjacencyList;

  public GameMap() {
    this.adjacencyList = new HashMap<>();
  }

  public void addTerritory(Territory territory) {
    adjacencyList.putIfAbsent(territory, new HashSet<>());
  }

  public void addConnection(Territory origin, Territory destination) {
    adjacencyList.get(origin).add(destination);
    adjacencyList.get(destination).add(origin);
  }


  public boolean isAttackable(Territory origin, Territory destination) {
    return origin.getOwner() != destination.getOwner()
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
    if (origin.getOwner() != destination.getOwner()) {
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
        if (next.getOwner() != current.getOwner()) continue;
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
