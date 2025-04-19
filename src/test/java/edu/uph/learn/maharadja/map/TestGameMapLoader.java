package edu.uph.learn.maharadja.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestGameMapLoader {
  @Test
  public void loadJsonFileToGameMap() {
    GameMap map = GameMapLoader.load(MapType.CLASSIC);

    assertEquals(8, map.getAllRegions().size());
    assertEquals(3, map.getAllRegions().getFirst().getBonusTroops());
    assertEquals(6, map.getTerritoriesByRegion(map.getAllRegions().getFirst()).size());
    assertEquals(6, map.getTerritoriesByRegion(map.getAllRegions().get(1)).size());

    Territory territory = map.getTerritoriesByRegion(map.getAllRegions().getFirst()).getFirst();
    assertEquals("Lamuri", territory.getName());
    assertEquals(2, map.adjacencyList.get(territory).size());
  }
}
