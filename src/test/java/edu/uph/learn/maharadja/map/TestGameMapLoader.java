package edu.uph.learn.maharadja.map;

import edu.uph.learn.maharadja.game.GameState;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestGameMapLoader {
  @Test
  public void loadJsonFileToGameMap() {
    GameState.init();
    GameMap map = GameMapLoader.load(MapType.CLASSIC);

    assertEquals(8, map.getAllRegions().size());
    assertEquals(3, map.getAllRegions().getFirst().getBonusTroops());

    Region sumatra = map.getAllRegions().getFirst();
    assertEquals(10, map.getTerritoriesByRegion(sumatra).size());
    Region malayPeninsula = map.getAllRegions().get(3);
    assertEquals(6, map.getTerritoriesByRegion(malayPeninsula).size());

    Territory lamuri = map.getTerritoryMap().get(new Point2D(0, 5));

    // Odd Row Adjacency
    assertEquals(1, lamuri.getR() % 2);
    assertEquals("Lamuri", lamuri.getName());
    Set<Territory> neighbor;

    neighbor = map.adjacencyList.get(lamuri);
    System.out.println("Lamuri > " + neighbor);
    assertTrue(neighbor.contains(new Territory(malayPeninsula, "Malacca")));
    assertTrue(neighbor.contains(new Territory(sumatra, "Barus")));
    assertEquals(2, neighbor.size());

    // Even Row Adjacency
    Territory barus = map.getTerritoryMap().get(new Point2D(1, 6));
    assertEquals(0, barus.getR() % 2);
    assertEquals("Barus", barus.getName());

    neighbor = map.adjacencyList.get(barus);
    System.out.println("Barus > " + neighbor);
    assertTrue(neighbor.contains(new Territory(sumatra, "Lamuri")));
    assertTrue(neighbor.contains(new Territory(sumatra, "Toba Highlands")));
    assertTrue(neighbor.contains(new Territory(sumatra, "Minangkabau")));
    assertEquals(3, neighbor.size());

  }
}
